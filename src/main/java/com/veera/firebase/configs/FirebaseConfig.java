package com.veera.firebase.configs;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() throws IOException {

        try {

            System.out.println("======================================");
            System.out.println("🔍 Checking classpath resource...");
            System.out.println("Configured path      : " + firebaseConfigPath);

            ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
            System.out.println("Resource exists?     : " + resource.exists());

            if (resource.exists()) {
                System.out.println("Resolved URL         : " + resource.getURL());
                System.out.println("Resource description : " + resource.getDescription());
            } else {
                System.out.println("⚠️ Resource NOT found on classpath. Check src/main/resources/" + firebaseConfigPath);
            }
            System.out.println("======================================");

            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials googleCredentials =GoogleCredentials.fromStream(
                        new ClassPathResource(firebaseConfigPath)
                                .getInputStream()
                );

                googleCredentials.refreshIfExpired();
                AccessToken token = googleCredentials.refreshAccessToken();
                System.out.println("✅ Access token obtained: " + token.getTokenValue().substring(0, 20) + "...");
                System.out.println("Expires at: " + token.getExpirationTime());
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                googleCredentials
                        )
                        .build();

                FirebaseApp.initializeApp(options);

                System.out.println("✅ FirebaseApp initialized.");
                System.out.println("Project ID from options : " + options.getProjectId());

            }

            // Verify Firestore connection
            Firestore firestore = FirestoreClient.getFirestore();

            // Simple API call to confirm authentication and connectivity
            firestore.listCollections().iterator().hasNext();

            System.out.println("======================================");
            System.out.println("✅ Firebase connected successfully.");
            System.out.println("Project ID : " + FirebaseApp.getInstance().getOptions().getProjectId());
            System.out.println("======================================");

        } catch (Exception e) {
            System.out.println("======================================");
            System.out.println("❌ Firebase connection failed.");
            e.printStackTrace();
            System.out.println("======================================");
        }

    }
}
