package org.example.evaluations.evaluation.clients;

import org.example.evaluations.evaluation.dtos.PayoutPurpose;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class RazorpayPaymentGatewayClient {

    @Value("${razorpay.id}")
    private String razorpayKeyId;

    @Value("${razorpay.secret}")
    private String razorpayKeySecret;

    @Autowired
    private final RestTemplateBuilder restTemplateBuilder;

    public RazorpayPaymentGatewayClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    private final String path = "https://api.razorpay.com/v1/payouts";

    public String createPayoutToBankAccount(String accountNumber, Double amount, PayoutPurpose purpose, String referenceId, String narration) {
        //Add your implementation here

        JSONObject payoutRequest = new JSONObject();
        payoutRequest.put("account_number", accountNumber);
        payoutRequest.put("amount", amount);
        payoutRequest.put("currency", "INR");
        payoutRequest.put("purpose", purpose.toString());
        payoutRequest.put("mode", "IMPS");
        payoutRequest.put("fund_account_id", "fa_00000000000001");
        payoutRequest.put("reference_id", referenceId);
        payoutRequest.put("narration", narration);
        payoutRequest.put("queue_if_low_balance", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(razorpayKeyId, razorpayKeySecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Payout-Idempotency", UUID.randomUUID().toString());

        HttpEntity<String> request = new HttpEntity<>(payoutRequest.toString(), headers);

        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
