package com.personal.awslambda.model;

import lombok.Builder;

@Builder
public record WebhookSecret(String webhookUrl) {
}
