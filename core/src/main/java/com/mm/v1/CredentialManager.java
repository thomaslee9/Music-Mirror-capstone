package com.mm.v1;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Base64;

public class CredentialManager {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_BYTES = 16; // 128 bits
    private static final Path KEY_FILE_PATH = Paths.get("encryption_key.txt");
    private static final Path CREDENTIALS_FILE_PATH = Paths.get("encrypted_credentials.txt");

    public static void main(String[] args) {
        try {
            // Generate or retrieve the encryption key
            byte[] encryptionKey = getOrCreateEncryptionKey();

            // Encrypt your credentials
            String username = "your_username";
            String password = "your_password";
            String credentials = username + ":" + password;
            String encryptedCredentials = encryptCredentials(credentials, encryptionKey);

            // Save the encrypted credentials to a file
            saveEncryptedCredentialsToFile(encryptedCredentials);

            // Decrypt the credentials and use them
            String decryptedCredentials = decryptCredentials(encryptionKey);
            System.out.println("Decrypted credentials: " + decryptedCredentials);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getOrCreateEncryptionKey() throws Exception {
        if (Files.exists(KEY_FILE_PATH)) {
            return Files.readAllBytes(KEY_FILE_PATH);
        } else {
            byte[] encryptionKey = generateRandomKey(KEY_SIZE_BYTES);
            Files.write(KEY_FILE_PATH, encryptionKey, StandardOpenOption.CREATE);
            return encryptionKey;
        }
    }

    public static byte[] generateRandomKey(int keySize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keySize];
        secureRandom.nextBytes(key);
        return key;
    }

    public static String encryptCredentials(String credentials, byte[] encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(credentials.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void saveEncryptedCredentialsToFile(String encryptedCredentials) throws Exception {
        Files.write(CREDENTIALS_FILE_PATH, encryptedCredentials.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    }

    public static String decryptCredentials(byte[] encryptionKey) throws Exception {
        String encryptedCredentials = new String(Files.readAllBytes(CREDENTIALS_FILE_PATH), StandardCharsets.UTF_8).trim();
        System.out.println("Encrypted credentials: " + encryptedCredentials);
    
        // Add padding if necessary
        int paddingLength = encryptedCredentials.length() % 4;
        if (paddingLength > 0) {
            encryptedCredentials += "=".repeat(4 - paddingLength);
        }
    
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCredentials));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
}
