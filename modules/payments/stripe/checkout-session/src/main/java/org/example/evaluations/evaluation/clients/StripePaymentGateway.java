package org.example.evaluations.evaluation.clients;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.evaluations.evaluation.dtos.SessionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StripePaymentGateway {

    @Value("${stripe.key}")
    public String apiKey;

    public SessionDto createSession(String successUrl, List<Long> amounts, List<String> productNames, List<Long> quantities) {
        //Add your implementation here
        Stripe.apiKey = apiKey;

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl);

        for (int i = 0; i < productNames.size(); i++) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("INR")
                                    .setUnitAmount(amounts.get(i))
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(productNames.get(i))
                                                    .build()
                                    )
                                    .build()
                    )
                    .setQuantity(quantities.get(i))
                    .build();

            paramsBuilder.addLineItem(lineItem);
        }

        Session session;

        try {
            session = Session.create(paramsBuilder.build());
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(session.getId());
        sessionDto.setTotal(session.getAmountTotal());
        sessionDto.setExpiry(session.getExpiresAt());
        sessionDto.setUrl(session.getUrl());

        return sessionDto;
    }
}
