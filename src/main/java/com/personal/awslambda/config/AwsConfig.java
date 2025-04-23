package com.personal.awslambda.config;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Module
public class AwsConfig {

    @Provides
    public Region getDefaultAwsRegion() {
        return Region.US_WEST_2;
    }

    @Provides
    public SecretsManagerClient getSecretsManagerClient(Region region) {
        return SecretsManagerClient.builder()
                .region(region)
                .build();
    }
}
