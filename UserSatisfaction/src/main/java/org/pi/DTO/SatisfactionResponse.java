package org.pi.DTO;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SatisfactionResponse", propOrder = {
        "satisfactionScore", "satisfactionLabel", "confidence", "explanation", "status"
})
public class SatisfactionResponse {

    @XmlElement(required = true)
    private double satisfactionScore;

    @XmlElement(required = true)
    private String satisfactionLabel;

    @XmlElement(required = true)
    private double confidence;

    @XmlElement(required = true)
    private String explanation;

    @XmlElement(required = true)
    private String status;

    // ── Required by JAXB ──────────────────────────────────────────────────────
    public SatisfactionResponse() {}

    // ── The constructor the service calls ─────────────────────────────────────
    public SatisfactionResponse(double satisfactionScore, String satisfactionLabel,
                                double confidence, String explanation, String status) {
        this.satisfactionScore = satisfactionScore;
        this.satisfactionLabel = satisfactionLabel;
        this.confidence        = confidence;
        this.explanation       = explanation;
        this.status            = status;
    }

    public static SatisfactionResponse error(String message) {
        return new SatisfactionResponse(0, "UNKNOWN", 0, message, "ERROR");
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public double getSatisfactionScore()       { return satisfactionScore; }
    public void setSatisfactionScore(double v) { this.satisfactionScore = v; }

    public String getSatisfactionLabel()       { return satisfactionLabel; }
    public void setSatisfactionLabel(String v) { this.satisfactionLabel = v; }

    public double getConfidence()              { return confidence; }
    public void setConfidence(double v)        { this.confidence = v; }

    public String getExplanation()             { return explanation; }
    public void setExplanation(String v)       { this.explanation = v; }

    public String getStatus()                  { return status; }
    public void setStatus(String v)            { this.status = v; }
}