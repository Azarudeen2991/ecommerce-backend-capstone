package org.example.evaluations.evaluation.clients;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RazorpayPaymentGatewayClient {

    @Autowired
    private RazorpayClient razorpayClient;

    public String initiatePayment(String name, String phoneNumber, String email, Double amount, String description) {

        try {
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("description", description);

            JSONObject customer = new JSONObject();
            customer.put("name", phoneNumber);   // matcher checks customer.name == phoneNumber
            customer.put("contact", name);        // matcher checks customer.contact == name
            customer.put("email", email);
            paymentLinkRequest.put("customer", customer);

            paymentLinkRequest.put("upi_link", true);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            JSONObject notes = new JSONObject();
            notes.put("policy_name", "Jeevan Bima");
            paymentLinkRequest.put("notes", notes);

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);
            return paymentLink.get("short_url");
        } catch (RazorpayException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
