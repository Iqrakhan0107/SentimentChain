package com.sentimentchain.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.sentimentchain.api",
        "com.sentimentchain.SentimentDAO",
        "com.sentimentchain.model",
        "com.sentimentchain.service"
})
public class SentimentChainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentimentChainApplication.class, args);
    }
}


