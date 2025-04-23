package com.personal.awslambda.fixtures;

import com.personal.awslambda.model.CloudwatchAlarmTrigger;
import software.amazon.awssdk.services.cloudwatch.model.StateValue;

import static com.personal.awslambda.fixtures.SlackFixtures.SAMPLE_WEBHOOK_URL;

public class AwsFixtures {

    public static final String SAMPLE_WEBHOOK_SECRET = String.format("{\"webhookUrl\": \"%s\"}", SAMPLE_WEBHOOK_URL);
    public static final String SUCCESS_RESULT = "Success";
    public static final String FAILED_RESULT = "Failed";
    public static final String ALARM_NAME = "lambda-demo-alarm";
    public static final String ALARM_TIME = "2023-08-04T12:36:15.490+0000";
    public static final String ALARM_REASON = "testingAlarm";

    public static final CloudwatchAlarmTrigger IN_ALARM_TRIGGER = CloudwatchAlarmTrigger.builder()
            .time(ALARM_TIME)
            .alarmData(CloudwatchAlarmTrigger.AlarmData.builder()
                    .state(CloudwatchAlarmTrigger.AlarmState.builder()
                            .value(StateValue.ALARM.toString())
                            .reason(ALARM_REASON)
                            .build())
                    .alarmName(ALARM_NAME)
                    .build())
            .build();
    public static final CloudwatchAlarmTrigger OUT_OF_ALARM_TRIGGER = CloudwatchAlarmTrigger.builder()
            .time(ALARM_TIME)
            .alarmData(CloudwatchAlarmTrigger.AlarmData.builder()
                    .state(CloudwatchAlarmTrigger.AlarmState.builder()
                            .value(StateValue.OK.toString())
                            .reason(ALARM_REASON)
                            .build())
                    .alarmName(ALARM_NAME)
                    .build())
            .build();
}
