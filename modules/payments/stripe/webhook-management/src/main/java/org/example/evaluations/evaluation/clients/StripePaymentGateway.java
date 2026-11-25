package org.example.evaluations.evaluation.clients;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.WebhookEndpoint;
import com.stripe.param.WebhookEndpointCreateParams;
import com.stripe.param.WebhookEndpointUpdateParams;
import org.example.evaluations.evaluation.dtos.Webhook;
import org.example.evaluations.evaluation.dtos.WebhookStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StripePaymentGateway {

    @Value("${stripe.key}")
    public String apiKey;

    public Webhook createWebhook(String url, List<String> events) {
        Stripe.apiKey = apiKey;

        List<WebhookEndpointCreateParams.EnabledEvent> enabledEvents = new ArrayList<>();
        for (String event : events) {
            enabledEvents.add(WebhookEndpointCreateParams.EnabledEvent.valueOf(event));
        }

        WebhookEndpointCreateParams webhookEndpointCreateParams = WebhookEndpointCreateParams.builder()
                .setUrl(url)
                .addAllEnabledEvent(enabledEvents)
                .build();

        WebhookEndpoint webhookEndpoint;

        try {

            webhookEndpoint = WebhookEndpoint.create(webhookEndpointCreateParams);
            Webhook webhook = new Webhook();
            webhook.setId(webhookEndpoint.getId());
            webhook.setSecret(webhookEndpoint.getSecret());
            webhook.setUrl(webhookEndpoint.getUrl());
            webhook.setEvents(webhookEndpoint.getEnabledEvents());
            webhook.setStatus(WebhookStatus.enabled);
            return webhook;

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe webhook", e);
        }
    }

    public Boolean deleteWebhook(String webhookId) {

        Stripe.apiKey = apiKey;
        try {

            WebhookEndpoint resource = WebhookEndpoint.retrieve(webhookId);
            WebhookEndpoint deletedResource = resource.delete();
            return deletedResource.getDeleted();

        } catch (StripeException e) {
            throw new RuntimeException("Failed to delete Stripe webhook", e);
        }

    }


    public Webhook updateWebhook(String updatedUrl, List<String> events, String webhookId) {

        Stripe.apiKey = apiKey;

        List<WebhookEndpointUpdateParams.EnabledEvent> enabledEvents = new ArrayList<>();
        for (String event : events) {
            enabledEvents.add(WebhookEndpointUpdateParams.EnabledEvent.valueOf(event));
        }

        WebhookEndpointUpdateParams webhookEndpointUpdateParams = WebhookEndpointUpdateParams.builder()
                .setUrl(updatedUrl)
                .addAllEnabledEvent(enabledEvents)
                .build();

        try {
            WebhookEndpoint resource = WebhookEndpoint.retrieve(webhookId);;
            WebhookEndpoint updatedResource = resource.update(webhookEndpointUpdateParams);
            Webhook webhook = new Webhook();
            webhook.setId(updatedResource.getId());
            webhook.setSecret(updatedResource.getSecret());
            webhook.setUrl(updatedResource.getUrl());
            webhook.setEvents(updatedResource.getEnabledEvents());
            webhook.setStatus(WebhookStatus.valueOf(updatedResource.getStatus()));
            return webhook;
        } catch (StripeException e) {
            throw new RuntimeException("Failed to update Stripe webhook", e);
        }

    }

}
