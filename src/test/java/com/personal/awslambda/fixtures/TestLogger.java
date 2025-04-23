package com.personal.awslambda.fixtures;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class TestLogger implements LambdaLogger {
    public void log(String s) {
    }

    public void log(byte[] bytes) {
    }
}
