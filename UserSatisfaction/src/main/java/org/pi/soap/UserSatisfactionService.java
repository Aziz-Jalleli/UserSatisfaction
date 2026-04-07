package org.pi.soap;


import org.pi.DTO.SatisfactionRequest;
import org.pi.DTO.SatisfactionResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

/**
 * JAX-WS Service Endpoint Interface (SEI)
 *
 * WSDL served at: http://localhost:8080/soap/satisfaction?wsdl
 */
@WebService(
        name            = "UserSatisfactionService",
        serviceName     = "UserSatisfactionService",
        portName        = "UserSatisfactionServicePort",
        targetNamespace = "http://soap.satisfaction.com/"
)
@SOAPBinding(
        style          = SOAPBinding.Style.DOCUMENT,
        use            = SOAPBinding.Use.LITERAL,
        parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
)
public interface UserSatisfactionService {

    /**
     * Predicts whether a user will be satisfied with a provider,
     * using DB-aggregated stats + review text + star/vote inputs.
     *
     * @param request contains businessId, reviewText, starsGiven, votes
     * @return satisfaction score (1–5), label, confidence, explanation
     */
    @WebMethod(operationName = "predictSatisfaction",
            action = "http://soap.satisfaction.com/predictSatisfaction")
    @WebResult(name = "satisfactionResponse")
    SatisfactionResponse predictSatisfaction(
            @WebParam(name = "satisfactionRequest") SatisfactionRequest request
    );
}
