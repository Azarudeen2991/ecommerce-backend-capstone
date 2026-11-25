package org.example.evaluations.evaluation.utils;

import org.example.evaluations.evaluation.dtos.CreateSubscriptionRequest;
import org.example.evaluations.evaluation.dtos.RazorpayCustomerContactDetails;
import org.example.evaluations.evaluation.dtos.RazorpayPlanRequest;
import org.example.evaluations.evaluation.dtos.RazorpaySubscriptionRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionRequestConverter {
    public RazorpaySubscriptionRequest from(CreateSubscriptionRequest createSubscriptionRequest) {
        //Add your implementation here
        RazorpayPlanRequest razorpayPlanRequest = new RazorpayPlanRequest();

        razorpayPlanRequest.setFrequency(createSubscriptionRequest.getPlanPeriod().toString());
        razorpayPlanRequest.setAmount(createSubscriptionRequest.getPlanAmount());
        razorpayPlanRequest.setDescription(createSubscriptionRequest.getPlanDescription());
        razorpayPlanRequest.setName(createSubscriptionRequest.getPlanTitle());

        RazorpayCustomerContactDetails razorpayCustomerContactDetails = new RazorpayCustomerContactDetails();
        razorpayCustomerContactDetails.setEmail(createSubscriptionRequest.getEmail());
        razorpayCustomerContactDetails.setPhoneNumber(createSubscriptionRequest.getPhone());

        return getRazorpaySubscriptionRequest(createSubscriptionRequest, razorpayPlanRequest, razorpayCustomerContactDetails);
    }

    @NotNull
    private static RazorpaySubscriptionRequest getRazorpaySubscriptionRequest(CreateSubscriptionRequest createSubscriptionRequest, RazorpayPlanRequest razorpayPlanRequest, RazorpayCustomerContactDetails razorpayCustomerContactDetails) {
        RazorpaySubscriptionRequest razorpaySubscriptionRequest = new RazorpaySubscriptionRequest();

        razorpaySubscriptionRequest.setTotalCount(createSubscriptionRequest.getTotalNumberOfBillingCycles());
        razorpaySubscriptionRequest.setRazorpayPlanRequest(razorpayPlanRequest);
        razorpaySubscriptionRequest.setRazorpayCustomerContactDetails(razorpayCustomerContactDetails);
        razorpaySubscriptionRequest.setStartTime(createSubscriptionRequest.getStarting());
        razorpaySubscriptionRequest.setExpiryTime(createSubscriptionRequest.getEnding());
        razorpaySubscriptionRequest.setQuantity(createSubscriptionRequest.getChargeCount());
        return razorpaySubscriptionRequest;
    }
}
