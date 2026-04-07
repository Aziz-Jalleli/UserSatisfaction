package org.pi.model;



public class BusinessStats {

    /** Yelp business_id */
    public final String businessId;

    /** Total number of reviews in the DB for this business */
    public final long reviewCount;

    /** Average star rating (1.0 – 5.0) */
    public final double avgStars;

    /** Standard deviation of star ratings (spread indicator) */
    public final double stddevStars;

    /** Average "useful" votes per review */
    public final double avgUseful;

    /** Average "funny" votes per review */
    public final double avgFunny;

    /** Average "cool" votes per review */
    public final double avgCool;

    /** Fraction of reviews that are 5-star (0.0 – 1.0) */
    public final double pct5Star;

    /** Fraction of reviews that are 4-star (0.0 – 1.0) */
    public final double pct4Star;

    /** Fraction of reviews that are 1 or 2-star (0.0 – 1.0) */
    public final double pctLow;

    public BusinessStats(String businessId, long reviewCount,
                         double avgStars, double stddevStars,
                         double avgUseful, double avgFunny, double avgCool,
                         double pct5Star, double pct4Star, double pctLow) {
        this.businessId  = businessId;
        this.reviewCount = reviewCount;
        this.avgStars    = avgStars;
        this.stddevStars = stddevStars;
        this.avgUseful   = avgUseful;
        this.avgFunny    = avgFunny;
        this.avgCool     = avgCool;
        this.pct5Star    = pct5Star;
        this.pct4Star    = pct4Star;
        this.pctLow      = pctLow;
    }

    /** True if there are enough reviews to make a reliable prediction. */
    public boolean hasSufficientData() {
        return reviewCount >= 5;
    }

    @Override
    public String toString() {
        return String.format(
                "BusinessStats{id=%s, count=%d, avgStars=%.2f, stddev=%.2f, " +
                        "useful=%.2f, cool=%.2f, pct5=%.1f%%, pctLow=%.1f%%}",
                businessId, reviewCount, avgStars, stddevStars,
                avgUseful, avgCool, pct5Star * 100, pctLow * 100
        );
    }
}