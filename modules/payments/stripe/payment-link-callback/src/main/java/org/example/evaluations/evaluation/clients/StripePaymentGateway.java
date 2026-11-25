package org.example.evaluations.evaluation.clients;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentGateway {

    @Value("${stripe.key}")
    public String apiKey;

    public String getPaymentLink(Long amount, Long quantity, String callbackUrl, String productName) {
        Stripe.apiKey = apiKey;

        try {
            // Step 1: Create a Product with the given name
            ProductCreateParams productParams = ProductCreateParams.builder()
                    .setName(productName)
                    .build();
            Product product = Product.create(productParams);

            // Step 2: Create a Price linked to that Product
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setProduct(product.getId())
                    .setCurrency("inr")
                    .setUnitAmount(amount)
                    .build();
            Price price = Price.create(priceParams);

            // Step 3: Create the Payment Link using the Price ID
            PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                    .addLineItem(
                            PaymentLinkCreateParams.LineItem.builder()
                                    .setPrice(price.getId())
                                    .setQuantity(quantity)
                                    .build()
                    )
                    .setAfterCompletion(
                            PaymentLinkCreateParams.AfterCompletion.builder()
                                    .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                    .setRedirect(
                                            PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                    .setUrl(callbackUrl)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment link");
        }
    }
}
