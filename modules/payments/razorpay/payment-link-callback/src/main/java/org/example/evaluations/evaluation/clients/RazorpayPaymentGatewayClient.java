package org.example.evaluations.evaluation.clients;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class RazorpayPaymentGatewayClient {

    @Autowired
    private RazorpayClient razorpayClient;

    public String completePaymentAndOpenCallBack(String name, String phoneNumber, String email, Double amount, String description, String orderId,URL callback) {

        try {
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("description", description);
            paymentLinkRequest.put("reference_id", orderId);
            paymentLinkRequest.put("callback_url", callback.toString());
            paymentLinkRequest.put("callback_method","get");
            //paymentLinkRequest.put("upi_link", true);

            JSONObject customer = new JSONObject();
            customer.put("name", phoneNumber);
            customer.put("email", email);
            customer.put("contact", name);
            paymentLinkRequest.put("customer", customer);

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
            throw new RuntimeException("Failed to create payment link");
        }
    }
}
