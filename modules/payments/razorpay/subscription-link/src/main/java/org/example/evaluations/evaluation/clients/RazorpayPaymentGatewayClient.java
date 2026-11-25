package org.example.evaluations.evaluation.clients;

import com.razorpay.Plan;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Subscription;
import org.example.evaluations.evaluation.dtos.RazorpayPlanRequest;
import org.example.evaluations.evaluation.dtos.RazorpaySubscriptionRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGatewayClient {

    @Autowired
    private RazorpayClient razorpayClient;

    private final String offerId = "offer_JTUADI4ZWBGWur";

    public Subscription createSubscriptionLink(RazorpaySubscriptionRequest subscriptionInput) {
        try {
            // Step 1: Create a Plan
            JSONObject planJson = getJsonObject(subscriptionInput);

            Plan plan = razorpayClient.plans.create(planJson);
            String planId = plan.get("id");

            // Step 2: Create a Subscription using the plan ID
            JSONObject subscriptionJson = getJsonObject(subscriptionInput, planId);

            return razorpayClient.subscriptions.create(subscriptionJson);

        } catch (RazorpayException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @NotNull
    private static JSONObject getJsonObject(RazorpaySubscriptionRequest subscriptionInput) {
        RazorpayPlanRequest razorpayPlanRequest = subscriptionInput.getRazorpayPlanRequest();

        JSONObject itemJson = new JSONObject();
        itemJson.put("name", razorpayPlanRequest.getName());
        itemJson.put("amount", razorpayPlanRequest.getAmount());
        itemJson.put("currency", "INR");
        itemJson.put("description", razorpayPlanRequest.getDescription());

        JSONObject planJson = new JSONObject();
        planJson.put("period", razorpayPlanRequest.getFrequency());
        planJson.put("interval", 1);
        planJson.put("item", itemJson);
        return planJson;
    }

    @NotNull
    private JSONObject getJsonObject(RazorpaySubscriptionRequest subscriptionInput, String planId) {
        JSONObject notifyInfo = new JSONObject();
        notifyInfo.put("notify_phone", subscriptionInput.getRazorpayCustomerContactDetails().getPhoneNumber());
        notifyInfo.put("notify_email", subscriptionInput.getRazorpayCustomerContactDetails().getEmail());

        JSONObject subscriptionJson = new JSONObject();
        subscriptionJson.put("plan_id", planId);
        subscriptionJson.put("total_count", subscriptionInput.getTotalCount());
        subscriptionJson.put("customer_notify", true);
        subscriptionJson.put("quantity", subscriptionInput.getQuantity());
        subscriptionJson.put("offer_id", offerId);
        subscriptionJson.put("start_at", subscriptionInput.getStartTime());
        subscriptionJson.put("expire_by", subscriptionInput.getExpiryTime());
        subscriptionJson.put("notify_info", notifyInfo);
        return subscriptionJson;
    }
}
