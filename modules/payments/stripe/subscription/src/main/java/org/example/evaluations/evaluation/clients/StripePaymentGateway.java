package org.example.evaluations.evaluation.clients;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.param.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentGateway {

    @Value("${stripe.key}")
    public String apiKey;

    private final Long trialDays = 730L;

    public String createSubscriptionForProduct(String customerName,String customerEmail,Long productAmount, String productName, PlanCreateParams.Interval interval) {
        Stripe.apiKey = apiKey;

        SubscriptionCreateParams.Item.PriceData.Recurring.Interval subscriptionInterval =
                SubscriptionCreateParams.Item.PriceData.Recurring.Interval.valueOf(interval.name());

        CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                .setName(customerName)
                .setEmail(customerEmail)
                .build();
        Customer customer;
        try {
            customer = Customer.create(customerCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        ProductCreateParams productCreateParams = ProductCreateParams.builder()
                .setName(productName)
                .build();
        Product product;
        try {
            product = Product.create(productCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        SubscriptionCreateParams subscriptionCreateParams = SubscriptionCreateParams.builder()
                .setCustomer(customer.getId())
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPriceData(
                                        SubscriptionCreateParams.Item.PriceData.builder()
                                                .setCurrency("INR")
                                                .setProduct(product.getId())
                                                .setUnitAmount(productAmount)
                                                .setRecurring(
                                                        SubscriptionCreateParams.Item.PriceData.Recurring.builder()
                                                                .setInterval(subscriptionInterval)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setTrialPeriodDays(trialDays)
                .build();
        try {
            Subscription subscription = Subscription.create(subscriptionCreateParams);
            return subscription.getId();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create subscription for product", e);
        }
    }
}
