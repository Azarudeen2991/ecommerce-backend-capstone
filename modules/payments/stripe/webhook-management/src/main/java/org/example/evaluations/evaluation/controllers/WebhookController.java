package org.example.evaluations.evaluation.controllers;

import org.example.evaluations.evaluation.dtos.Webhook;
import org.example.evaluations.evaluation.dtos.WebhookDto;
import org.example.evaluations.evaluation.services.IWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {

    @Autowired
    private IWebhookService webhookService;

    @PostMapping("/webhook")
    public Webhook createWebhook(@RequestBody WebhookDto webhookDto) {
        return webhookService.createWebhook(webhookDto.getUrl(), webhookDto.getEvents());
    }

    @DeleteMapping("/webhook/{id}")
    public Boolean deleteWebhook(@PathVariable String id) {
        return webhookService.deleteWebhook(id);
    }

    @PatchMapping("/webhook/{id}")
    public Webhook updateWebhook(@PathVariable String id, @RequestBody WebhookDto webhookDto) {
        return webhookService.updateWebhook(webhookDto.getUrl(), webhookDto.getEvents(), id);
    }
}
