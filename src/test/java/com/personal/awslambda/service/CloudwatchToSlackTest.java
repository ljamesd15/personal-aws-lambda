package com.personal.awslambda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.awslambda.fixtures.TestContext;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.io.IOException;

import static com.personal.awslambda.fixtures.AwsFixtures.ALARM_NAME;
import static com.personal.awslambda.fixtures.AwsFixtures.ALARM_REASON;
import static com.personal.awslambda.fixtures.AwsFixtures.ALARM_TIME;
import static com.personal.awslambda.fixtures.AwsFixtures.FAILED_RESULT;
import static com.personal.awslambda.fixtures.AwsFixtures.IN_ALARM_TRIGGER;
import static com.personal.awslambda.fixtures.AwsFixtures.OUT_OF_ALARM_TRIGGER;
import static com.personal.awslambda.fixtures.AwsFixtures.SAMPLE_WEBHOOK_SECRET;
import static com.personal.awslambda.fixtures.AwsFixtures.SUCCESS_RESULT;
import static com.personal.awslambda.fixtures.SlackFixtures.SAMPLE_WEBHOOK_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CloudwatchToSlackTest {

    @Test
    public void handleRequestWithMonitorInAlarmTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();
        ArgumentCaptor<GetSecretValueRequest> getSecretValueRequestArgumentCaptor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
        ArgumentCaptor<Payload> payloadArgumentCaptor = ArgumentCaptor.forClass(Payload.class);
        ArgumentCaptor<String> webhookCaptor = ArgumentCaptor.forClass(String.class);

        GetSecretValueResponse mockedSecretValueResponse = GetSecretValueResponse.builder()
                .secretString(SAMPLE_WEBHOOK_SECRET)
                .build();
        WebhookResponse mockedWebhookResponse = WebhookResponse.builder()
                .code(200)
                .build();
        when(mockSecretClient.getSecretValue(getSecretValueRequestArgumentCaptor.capture()))
                .thenReturn(mockedSecretValueResponse);
        when(mockSlack.send(webhookCaptor.capture(), payloadArgumentCaptor.capture()))
                .thenReturn(mockedWebhookResponse);

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        String result = underTest.handleRequest(IN_ALARM_TRIGGER, new TestContext());

        Assert.assertEquals(SUCCESS_RESULT, result);
        Assert.assertEquals("ga-slack-webhook", getSecretValueRequestArgumentCaptor.getValue().secretId());
        Assert.assertEquals(SAMPLE_WEBHOOK_URL, webhookCaptor.getValue());
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_NAME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains("triggered"));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_TIME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_REASON));
    }

    @Test
    public void handleRequestWithMonitorOutOfAlarmTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();
        ArgumentCaptor<GetSecretValueRequest> getSecretValueRequestArgumentCaptor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
        ArgumentCaptor<Payload> payloadArgumentCaptor = ArgumentCaptor.forClass(Payload.class);
        ArgumentCaptor<String> webhookCaptor = ArgumentCaptor.forClass(String.class);

        GetSecretValueResponse mockedSecretValueResponse = GetSecretValueResponse.builder()
                .secretString(SAMPLE_WEBHOOK_SECRET)
                .build();
        WebhookResponse mockedWebhookResponse = WebhookResponse.builder()
                .code(200)
                .build();
        when(mockSecretClient.getSecretValue(getSecretValueRequestArgumentCaptor.capture()))
                .thenReturn(mockedSecretValueResponse);
        when(mockSlack.send(webhookCaptor.capture(), payloadArgumentCaptor.capture()))
                .thenReturn(mockedWebhookResponse);

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        String result = underTest.handleRequest(OUT_OF_ALARM_TRIGGER, new TestContext());

        Assert.assertEquals(SUCCESS_RESULT, result);
        Assert.assertEquals("ga-slack-webhook", getSecretValueRequestArgumentCaptor.getValue().secretId());
        Assert.assertEquals(SAMPLE_WEBHOOK_URL, webhookCaptor.getValue());
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_NAME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains("resolved"));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_TIME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_REASON));
    }

    @Test
    public void handleRequestWithFailingToPostToSlackTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();
        ArgumentCaptor<GetSecretValueRequest> getSecretValueRequestArgumentCaptor = ArgumentCaptor.forClass(GetSecretValueRequest.class);
        ArgumentCaptor<Payload> payloadArgumentCaptor = ArgumentCaptor.forClass(Payload.class);
        ArgumentCaptor<String> webhookCaptor = ArgumentCaptor.forClass(String.class);

        GetSecretValueResponse mockedSecretValueResponse = GetSecretValueResponse.builder()
                .secretString(SAMPLE_WEBHOOK_SECRET)
                .build();
        WebhookResponse mockedWebhookResponse = WebhookResponse.builder()
                .code(400)
                .build();
        when(mockSecretClient.getSecretValue(getSecretValueRequestArgumentCaptor.capture()))
                .thenReturn(mockedSecretValueResponse);
        when(mockSlack.send(webhookCaptor.capture(), payloadArgumentCaptor.capture()))
                .thenReturn(mockedWebhookResponse);

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        String result = underTest.handleRequest(IN_ALARM_TRIGGER, new TestContext());

        Assert.assertEquals(FAILED_RESULT, result);
        Assert.assertEquals("ga-slack-webhook", getSecretValueRequestArgumentCaptor.getValue().secretId());
        Assert.assertEquals(SAMPLE_WEBHOOK_URL, webhookCaptor.getValue());
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_NAME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains("triggered"));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_TIME));
        Assert.assertTrue(payloadArgumentCaptor.getValue().getText().contains(ALARM_REASON));
    }


    @Test
    public void handleRequestWithFailingToPostToSlackAndExceptionTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();
        ArgumentCaptor<GetSecretValueRequest> getSecretValueRequestArgumentCaptor = ArgumentCaptor.forClass(GetSecretValueRequest.class);

        GetSecretValueResponse mockedSecretValueResponse = GetSecretValueResponse.builder()
                .secretString(SAMPLE_WEBHOOK_SECRET)
                .build();
        when(mockSecretClient.getSecretValue(getSecretValueRequestArgumentCaptor.capture()))
                .thenReturn(mockedSecretValueResponse);
        when(mockSlack.send(anyString(), any(Payload.class)))
                .thenThrow(new IOException("Testing"));

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        String result = underTest.handleRequest(IN_ALARM_TRIGGER, new TestContext());

        Assert.assertEquals(FAILED_RESULT, result);
        Assert.assertEquals("ga-slack-webhook", getSecretValueRequestArgumentCaptor.getValue().secretId());
    }

    @Test
    public void handleRequestWithUnableToFetchSecretTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();

        when(mockSecretClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenThrow(SecretsManagerException.create("Testing", new IOException()));

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        try {
            underTest.handleRequest(OUT_OF_ALARM_TRIGGER, new TestContext());
        } catch (RuntimeException ex) {
            Assert.assertEquals("Unable to fetch secret: ga-slack-webhook", ex.getMessage());
        }

        verify(mockSlack, never()).send(anyString(), any(Payload.class));
    }

    @Test
    public void handleRequestWithUnableToParseSecretTest() throws IOException {
        SecretsManagerClient mockSecretClient = mock(SecretsManagerClient.class);
        Slack mockSlack = mock(Slack.class);
        ObjectMapper mapper = new ObjectMapper();

        when(mockSecretClient.getSecretValue(any(GetSecretValueRequest.class)))
                .thenReturn(GetSecretValueResponse.builder().secretString("someValue").build());

        CloudwatchToSlack underTest = new CloudwatchToSlack(mockSecretClient, mockSlack, mapper);
        try {
            underTest.handleRequest(OUT_OF_ALARM_TRIGGER, new TestContext());
        } catch (RuntimeException ex) {
            Assert.assertEquals("Unable to parse secret: ga-slack-webhook", ex.getMessage());
        }

        verify(mockSlack, never()).send(anyString(), any(Payload.class));
    }
}
