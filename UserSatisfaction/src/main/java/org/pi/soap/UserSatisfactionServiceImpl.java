package org.pi.soap;

import org.pi.DTO.SatisfactionRequest;
import org.pi.DTO.SatisfactionResponse;
import org.pi.service.SatisfactionScoringService;
import io.quarkiverse.cxf.annotation.CXFEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jws.WebService;
import org.jboss.logging.Logger;

@ApplicationScoped
@CXFEndpoint("/satisfaction")
@WebService(
        serviceName     = "UserSatisfactionService",
        portName        = "UserSatisfactionServicePort",
        targetNamespace = "http://soap.satisfaction.com/"
)
public class UserSatisfactionServiceImpl implements UserSatisfactionService {

    private static final Logger LOG = Logger.getLogger(UserSatisfactionServiceImpl.class);

    @Inject
    SatisfactionScoringService scoringService;

    @Override
    public SatisfactionResponse predictSatisfaction(SatisfactionRequest request) {
        LOG.infof("predictSatisfaction — stars=%.1f text='%s'",
                request.getStarsGiven(),
                request.getReviewText() == null ? "" : request.getReviewText().substring(
                        0, Math.min(50, request.getReviewText().length())));
        try {
            if (request.getReviewText() == null || request.getReviewText().isBlank())
                return SatisfactionResponse.error("reviewText is required");
            if (request.getStarsGiven() < 1 || request.getStarsGiven() > 5)
                return SatisfactionResponse.error("starsGiven must be between 1 and 5");

            return scoringService.predict(request);

        } catch (Exception e) {
            LOG.errorf(e, "Unexpected error");
            return SatisfactionResponse.error("Internal error: " + e.getMessage());
        }
    }
}