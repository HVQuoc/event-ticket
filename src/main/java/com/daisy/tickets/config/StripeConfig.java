package com.daisy.tickets.config;


import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);
    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {

        logger.info("Stripe Secret Key: {}", secretKey); // Log the key (avoid in production)
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("Stripe secret key is not configured properly");
        }
        Stripe.apiKey = secretKey; // Set the Stripe API key
    }
}
