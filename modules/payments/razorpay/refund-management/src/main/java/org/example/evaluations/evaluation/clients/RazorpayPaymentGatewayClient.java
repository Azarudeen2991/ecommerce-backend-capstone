package org.example.evaluations.evaluation.clients;

import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import com.razorpay.RefundClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGatewayClient {

    @Autowired
    private RazorpayClient razorpayClient;

    private final String paymentId = "pay_39QqoUAi66xm2f"; //use this paymentId, wherever needed

    public String issueInstantRefund(Double amount, String receipt) {

        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount);
            refundRequest.put("receipt", receipt);
            refundRequest.put("speed", "optimum");

            JSONObject notes = new JSONObject();
            notes.put("notes_key_1", "Tea, Earl Grey, Hot");
            refundRequest.put("notes", notes);

            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
            return refund.get("id").toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String updateRefund(String refundId, JSONObject jsonObject) {
        try {
            RefundClient refundClient = razorpayClient.refunds;
            Refund refund = refundClient.edit(refundId, jsonObject);
            return refund.get("id").toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
