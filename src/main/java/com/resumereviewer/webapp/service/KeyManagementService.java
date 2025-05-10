package com.resumereviewer.webapp.service;

import com.resumereviewer.webapp.util.KeyPairGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class KeyManagementService {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public KeyManagementService() {
        KeyPair keyPair = KeyPairGeneratorUtil.generateKeyPair();
        if (keyPair != null) {
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
            publicKey = (RSAPublicKey) keyPair.getPublic();
        }
    }


    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyInPEM() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
            byte[] publicKeyBytes = keySpec.getEncoded();
            return Base64.getEncoder().encodeToString(publicKeyBytes);
        } catch (Exception e) {
            log.error("Failed to convert public key to PEM format", e);
            throw new RuntimeException("Public key conversion failed", e);
        }
    }

    private RSAPrivateKey parsePrivateKey(String privateKeyPEM) throws Exception {
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public String decryptPassword(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to decrypt password", e);
            throw new RuntimeException("Password decryption failed", e);
        }
    }
}