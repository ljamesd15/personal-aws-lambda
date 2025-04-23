package com.personal.awslambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.personal.awslambda.component.CloudwatchToSlackComponent;
import com.personal.awslambda.component.DaggerCloudwatchToSlackComponent;
import com.personal.awslambda.model.CloudwatchAlarmTrigger;
import com.personal.awslambda.service.CloudwatchToSlack;

/**
 * Class to handle messages sent to it by Cloudwatch.
 */
public class CloudwatchAlarmHandler implements RequestHandler<CloudwatchAlarmTrigger, String> {

    private final CloudwatchToSlack cloudwatchToSlack;

    public CloudwatchAlarmHandler() {
        CloudwatchToSlackComponent component = DaggerCloudwatchToSlackComponent.create();
        this.cloudwatchToSlack = component.cloudwatchToSlack();
    }

    /**
     * Handles the messages sent to it by Cloudwatch alarms and sends it to the Slack webhook.
     *
     * @param input The cloudwatch alarm trigger input
     * @param context Context of the invocation
     */
    public String handleRequest(CloudwatchAlarmTrigger input, Context context) {
        return this.cloudwatchToSlack.handleRequest(input, context);
    }
}
