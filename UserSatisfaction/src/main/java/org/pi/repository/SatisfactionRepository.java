package org.pi.repository;

import org.pi.model.ReviewT;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

/**
 * SatisfactionRepository
 * ──────────────────────
 * Runs aggregate SQL queries against the reviews table and returns
 * raw Object[] rows directly — no intermediate DTO needed.
 *
 * Row layout (used by SatisfactionScoringService):
 *   [0] review_count  (long)
 *   [1] avg_stars     (double)
 *   [2] stddev_stars  (double)
 *   [3] avg_useful    (double)
 *   [4] avg_cool      (double)
 *   [5] pct_5star     (double)
 *   [6] pct_low       (double)
 */
@ApplicationScoped
public class SatisfactionRepository implements PanacheRepository<ReviewT> {

    @Inject
    EntityManager em;

    /**
     * Queries aggregate stats for a business directly from the reviews table.
     *
     * @param businessId Yelp business_id
     * @return Optional.empty() if no reviews found for this business
     */
    public Optional<Object[]> getAggregateStats(String businessId) {
        try {
            Object[] row = (Object[]) em.createNativeQuery("""
                    SELECT
                        COUNT(*)                                          AS review_count,
                        AVG(stars)                                        AS avg_stars,
                        COALESCE(STDDEV(stars), 0)                       AS stddev_stars,
                        AVG(useful)                                       AS avg_useful,
                        AVG(cool)                                         AS avg_cool,
                        AVG(CASE WHEN stars = 5 THEN 1.0 ELSE 0.0 END)  AS pct_5star,
                        AVG(CASE WHEN stars <= 2 THEN 1.0 ELSE 0.0 END) AS pct_low
                    FROM reviewst
                    WHERE business_id = ?1
                    GROUP BY business_id
                    """)
                    .setParameter(1, businessId)
                    .getSingleResult();

            return Optional.of(row);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}