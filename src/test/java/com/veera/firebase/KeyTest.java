package com.veera.firebase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyTest {

    @Test
    public void testKeyVerification() throws Exception {
        System.out.println("=== STARTING KEY CRYPTOGRAPHIC VERIFICATION ===");

        // 1. Load the JSON credentials
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("fir-test-and-practice-firebase-adminsdk-fbsvc-da8a304552.json");
        if (is == null) {
            System.out.println("❌ Could not find credentials JSON file on classpath.");
            return;
        }

        JsonNode root = mapper.readTree(is);
        String privateKeyPem = root.get("private_key").asText();
        String privateKeyId = root.get("private_key_id").asText();
        String certUrl = root.get("client_x509_cert_url").asText();

        System.out.println("Key ID: " + privateKeyId);
        System.out.println("Cert URL: " + certUrl);

        // 2. Parse the Private Key
        String privateKeyPemClean = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPemClean);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(keySpec);
        System.out.println("✅ Private key parsed successfully. Algorithm: " + privateKey.getAlgorithm());

        // 3. Sign a test message
        byte[] message = "test-sign-message-12345".getBytes("UTF-8");
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message);
        byte[] signatureBytes = sign.sign();
        System.out.println("✅ Signed test message locally. Signature size: " + signatureBytes.length);

        // 4. Download Public Certificates from Google
        System.out.println("🌐 Downloading public certificates from Google...");
        JsonNode certsJson;
        try (InputStream certStream = new URL(certUrl).openStream()) {
            certsJson = mapper.readTree(certStream);
        }

        // 5. Look for the matching certificate
        X509Certificate certificate = null;
        Iterator<Map.Entry<String, JsonNode>> fields = certsJson.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().equals(privateKeyId)) {
                String certPem = field.getValue().asText();
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                java.io.ByteArrayInputStream certIs = new java.io.ByteArrayInputStream(certPem.getBytes("UTF-8"));
                certificate = (X509Certificate) cf.generateCertificate(certIs);
                break;
            }
        }

        if (certificate == null) {
            System.out.println("❌ ERROR: The key ID '" + privateKeyId + "' was NOT found in the certificates returned by Google.");
            System.out.println("This means this key has been DELETED or REVOKED in the Google Cloud Console / Firebase Console.");
            System.out.println("Google does not recognize this key anymore, which is why it returned 'Invalid JWT Signature'.");
            return;
        }

        System.out.println("✅ Found matching public certificate for Key ID: " + privateKeyId);

        // 6. Verify signature using public key from certificate
        Signature verifySign = Signature.getInstance("SHA256withRSA");
        verifySign.initVerify(certificate.getPublicKey());
        verifySign.update(message);
        boolean verified = verifySign.verify(signatureBytes);

        if (verified) {
            System.out.println("✅ SUCCESS: Cryptographic signature verified locally against Google's public certificate!");
            System.out.println("The private key in your JSON file is mathematically correct and matches the active certificate on Google's servers.");
            System.out.println("If Google OAuth server is still failing with 'Invalid JWT Signature', it might be a temporary Google-side issue, or clock-related issue on your computer.");
        } else {
            System.out.println("❌ ERROR: Signature verification FAILED locally.");
            System.out.println("This means the private key in your JSON file does NOT match the public certificate registered on Google.");
            System.out.println("The file is likely corrupted or modified.");
        }
    }
}
