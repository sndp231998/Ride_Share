package com.ride_share.config;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;


@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;  // e.g., "firebase/serviceAccountKey.json"

    @Bean
    FirebaseMessaging firebaseMessaging() {
        try {
            InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("FirebaseApp initialized successfully");
            } else {
                logger.info("FirebaseApp already initialized: {}", FirebaseApp.getInstance().getName());
            }

            return FirebaseMessaging.getInstance();

        } catch (IOException e) {
            logger.error("Failed to initialize FirebaseMessaging", e);
            throw new RuntimeException("Failed to initialize FirebaseMessaging", e);
        }
    }
}
