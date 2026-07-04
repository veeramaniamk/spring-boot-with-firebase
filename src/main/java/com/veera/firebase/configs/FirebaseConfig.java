package com.veera.firebase.configs;

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
                java.io.InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount)
                        /*.createScoped(java.util.Arrays.asList(
                                "https://www.googleapis.com/auth/cloud-platform",
                                "https://www.googleapis.com/auth/datastore"
                        ))*/;

                FirebaseOptions.Builder builder = FirebaseOptions.builder()
                        .setCredentials(credentials);

                // Explicitly set the project ID to avoid null project ID issues
                if (credentials instanceof com.google.auth.oauth2.ServiceAccountCredentials) {
                    builder.setProjectId(((com.google.auth.oauth2.ServiceAccountCredentials) credentials).getProjectId());
                }

                FirebaseOptions options = builder.build();

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
