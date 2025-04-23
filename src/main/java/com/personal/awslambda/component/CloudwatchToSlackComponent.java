package com.personal.awslambda.component;

import com.personal.awslambda.config.AwsConfig;
import com.personal.awslambda.config.SerializationConfig;
import com.personal.awslambda.config.SlackConfig;
import com.personal.awslambda.service.CloudwatchToSlack;
import dagger.Component;

@Component(modules = { AwsConfig.class, SerializationConfig.class, SlackConfig.class })
public interface CloudwatchToSlackComponent {
    CloudwatchToSlack cloudwatchToSlack();
}
