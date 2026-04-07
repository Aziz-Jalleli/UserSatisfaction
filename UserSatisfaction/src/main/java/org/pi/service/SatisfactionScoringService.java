package org.pi.service;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.pi.DTO.SatisfactionRequest;
import org.pi.DTO.SatisfactionResponse;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class SatisfactionScoringService {

    private StanfordCoreNLP pipeline;

    private static final double STARS_MEAN = 3.75;
    private static final double STARS_STD  = 1.48;

    @PostConstruct
    void init() {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public SatisfactionResponse predict(SatisfactionRequest request) {
        double stars    = request.getStarsGiven();
        String text     = request.getReviewText();

        double sentiment = analyzeWithStanfordNLP(text);

        double starComponent      = stars;
        double sentimentComponent = STARS_MEAN + sentiment * STARS_STD;

        double score = 0.70 * starComponent + 0.30 * sentimentComponent;
        score = Math.max(1.0, Math.min(5.0, score));
        score = Math.round(score * 10.0) / 10.0;

        String label       = toLabel(score);
        double confidence  = computeConfidence(stars, text, sentiment);
        String explanation = buildExplanation(stars, sentiment, score, label);

        return new SatisfactionResponse(score, label, confidence, explanation, "SUCCESS");
    }

    /**
     * Runs Stanford CoreNLP sentiment analysis on the review text.
     *
     * Stanford uses a Recursive Neural Tensor Network (RNTN) trained on the
     * Stanford Sentiment Treebank. It classifies each sentence as:
     *   Very Negative / Negative / Neutral / Positive / Very Positive
     *
     * We average across all sentences and map to [-1, +1]:
     *   Very Negative → -1.0
     *   Negative      → -0.5
     *   Neutral       →  0.0
     *   Positive      → +0.5
     *   Very Positive → +1.0
     */
    private double analyzeWithStanfordNLP(String text) {
        if (text == null || text.isBlank()) return 0.0;

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences == null || sentences.isEmpty()) return 0.0;

        double total = 0.0;
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            total += switch (sentiment) {
                case "Very negative" -> -1.0;
                case "Negative"      -> -0.5;
                case "Neutral"       ->  0.0;
                case "Positive"      ->  0.5;
                case "Very positive" ->  1.0;
                default              ->  0.0;
            };
        }

        return Math.max(-1.0, Math.min(1.0, total / sentences.size()));
    }

    private double computeConfidence(double stars, String text, double sentiment) {
        double conf = 50.0;
        if (stars >= 1 && stars <= 5) conf += 25.0;
        // Confidence is higher when stars and sentiment agree
        boolean agree = (stars >= 4 && sentiment > 0) || (stars <= 2 && sentiment < 0);
        if (agree) conf += 20.0;
        else conf += 5.0;
        int words = text == null ? 0 : text.split("\\s+").length;
        if (words > 20) conf += 4.0;
        return Math.min(99.0, Math.round(conf * 10.0) / 10.0);
    }

    private String toLabel(double score) {
        if (score < 2.0) return "VERY_DISSATISFIED";
        if (score < 3.0) return "DISSATISFIED";
        if (score < 3.5) return "NEUTRAL";
        if (score < 4.5) return "SATISFIED";
        return "VERY_SATISFIED";
    }

    private String buildExplanation(double stars, double sentiment, double score, String label) {
        String sentimentText = sentiment >  0.4 ? "very positive"
                : sentiment >  0.1 ? "positive"
                : sentiment < -0.4 ? "very negative"
                : sentiment < -0.1 ? "negative"
                : "neutral";

        String conclusion = switch (label) {
            case "VERY_SATISFIED"    -> "The user is very likely satisfied with this provider.";
            case "SATISFIED"         -> "The user is likely satisfied with this provider.";
            case "NEUTRAL"           -> "The user has mixed feelings about this provider.";
            case "DISSATISFIED"      -> "The user is likely dissatisfied with this provider.";
            case "VERY_DISSATISFIED" -> "The user is very unlikely to be satisfied with this provider.";
            default -> "Prediction complete.";
        };

        return String.format(
                "The user gave %.0f star(s). Stanford NLP detected a %s sentiment in the review text. %s",
                stars, sentimentText, conclusion
        );
    }
}