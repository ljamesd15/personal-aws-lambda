package com.personal.awslambda.model;

import java.util.List;
import lombok.Builder;

// https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html#alarms-and-actions
@Builder
public record CloudwatchAlarmTrigger(String source,
                                     String alarmArn,
                                     String accountId,
                                     String time,
                                     String region,
                                     AlarmData alarmData) {
    @Builder
    public record AlarmData(String alarmName,
                            AlarmState state,
                            AlarmState previousState,
                            AlarmConfiguration configuration) {}

    @Builder
    public record AlarmState(String value,
                             String reason,
                             String reasonData,
                             String timestamp) {}

    @Builder
    public record AlarmConfiguration(String description,
                                     List<Metric> metrics) {}

    @Builder
    public record Metric(String id,
                         AlarmMetricStat metricStat,
                         Boolean returnData) {}

    @Builder
    public record AlarmMetricStat(Metric metric) {}

    @Builder
    public record MetricInfo(String namespace,
                             String name,
                             Dimension dimensions) {}

    @Builder
    public record Dimension(String InstanceId) {}
}
