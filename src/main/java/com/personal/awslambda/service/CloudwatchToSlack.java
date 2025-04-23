package com.personal.awslambda.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.awslambda.model.CloudwatchAlarmTrigger;
import com.personal.awslambda.model.WebhookSecret;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import java.io.IOException;
import javax.inject.Inject;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.cloudwatch.model.StateValue;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class CloudwatchToSlack {
    private static final String WEBHOOK_SECRET = "ga-slack-webhook";
    private static final String IN_ALARM_REPORT_FORMAT = "Alarm: %s, triggered at %s for reason: %s.";
    private static final String OUT_OF_ALARM_REPORT_FORMAT = "Alarm: %s, resolved at %s for reason: %s.";

    private final SecretsManagerClient secretsManagerClient;
    private final Slack slack;
    private final ObjectMapper objectMapper;

    @Inject
    public CloudwatchToSlack(SecretsManagerClient secretsManagerClient, Slack slack, ObjectMapper objectMapper) {
        this.secretsManagerClient = secretsManagerClient;
        this.slack = slack;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles the messages sent to it by Cloudwatch alarms and sends it to the Slack webhook.
     *
     * @param input The cloudwatch alarm trigger input
     * @param context Context of the invocation
     */
    public String handleRequest(CloudwatchAlarmTrigger input, Context context) {
        // Create payload
        final String webhookUrl = this.getWebhook(WEBHOOK_SECRET);
        Payload.PayloadBuilder payloadBuilder = Payload.builder();
        if (input.alarmData().state().value().equals(StateValue.OK.toString())) {
            payloadBuilder.text(String.format(OUT_OF_ALARM_REPORT_FORMAT,
                    input.alarmData().alarmName(),
                    input.time(),
                    input.alarmData().state().reason())
            );
        } else {
            payloadBuilder.text(String.format(IN_ALARM_REPORT_FORMAT,
                    input.alarmData().alarmName(),
                    input.time(),
                    input.alarmData().state().reason())
            );
        }

        // Send to slack
        boolean success = this.sendToSlack(webhookUrl, payloadBuilder.build());
        if (!success) {
            context.getLogger().log("Unable to send payload to Slack");
        }
        return success ? "Success" : "Failed";
    }

    public String getWebhook(final String secretName) {
        try {
            final GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();
            final String stringifiedWebhook = this.secretsManagerClient.getSecretValue(valueRequest).secretString();
            final WebhookSecret webhookSecret = this.objectMapper.readValue(stringifiedWebhook, WebhookSecret.class);
            return webhookSecret.webhookUrl();
        } catch (SdkException ex) {
            throw new RuntimeException(String.format("Unable to fetch secret: %s", secretName), ex);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(String.format("Unable to parse secret: %s", secretName), ex);
        }
    }

    public boolean sendToSlack(final String webhookUrl, final Payload payload) {
        try {
            final WebhookResponse response = this.slack.send(webhookUrl, payload);
            return response.getCode() == 200;
        } catch (IOException ex) {
            return false;
        }
    }
}
