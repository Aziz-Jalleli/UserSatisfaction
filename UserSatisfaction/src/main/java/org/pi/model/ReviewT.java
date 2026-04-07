package org.pi.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code reviews} table.
 * Maps 1:1 to the Yelp CSV columns loaded into PostgreSQL.
 */
@Entity
@Table(
        name = "reviewst",
        indexes = {
                @Index(name = "idx_reviews_business_id", columnList = "business_id"),
                @Index(name = "idx_reviews_user_id",     columnList = "user_id"),
                @Index(name = "idx_reviews_date",         columnList = "review_date DESC")
        }
)
public class ReviewT extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /** Yelp's own review identifier (22-char alphanumeric) */
    @Column(name = "review_id", nullable = false, unique = true, length = 22)
    public String reviewId;

    /** Yelp user who wrote the review */
    @Column(name = "user_id", length = 22)
    public String userId;

    /** Yelp business being reviewed (provider/prestataire) */
    @Column(name = "business_id", nullable = false, length = 22)
    public String businessId;

    /** Star rating given by the user (1.0 – 5.0) */
    @Column(nullable = false)
    public Double stars;

    /** How many users found this review "useful" */
    @Column(nullable = false)
    public Double useful = 0.0;

    /** How many users found this review "funny" */
    @Column(nullable = false)
    public Double funny = 0.0;

    /** How many users found this review "cool" */
    @Column(nullable = false)
    public Double cool = 0.0;

    /** Full text of the review */
    @Column(name = "review_text", nullable = false, columnDefinition = "TEXT")
    public String reviewText;

    /** Date the review was posted */
    @Column(name = "review_date")
    public LocalDateTime reviewDate;
}
