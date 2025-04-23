package com.personal.awslambda.config;

import com.slack.api.Slack;
import dagger.Module;
import dagger.Provides;

@Module
public class SlackConfig {

    @Provides
    public Slack getSlackApi() {
        return Slack.getInstance();
    }
}
