package org.pi.DTO;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SatisfactionRequest", propOrder = {"reviewText", "starsGiven"})
public class SatisfactionRequest {

    @XmlElement(required = true)
    private String reviewText;

    @XmlElement(required = true)
    private double starsGiven;

    public SatisfactionRequest() {}

    public String getReviewText()       { return reviewText; }
    public void setReviewText(String v) { this.reviewText = v; }

    public double getStarsGiven()       { return starsGiven; }
    public void setStarsGiven(double v) { this.starsGiven = v; }
}