package com.api.GestionaFacilRestaurants.utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;

public class KeyUtil {
    @Value("${security.jwt.secret-key}")
    private static String privateKey;

   

    // Método para encriptar texto utilizando una llave privada
    public static String encrypt(String plainText) {
        try {
            SecretKey secretKey = getKeyFromPrivateKey(String.valueOf(privateKey));
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base58Util.encode(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    // Método para desencriptar texto utilizando una llave privada
    public static String decrypt(String encryptedText) {
        try {
            SecretKey secretKey = getKeyFromPrivateKey(String.valueOf(privateKey));
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base58Util.decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            //throw new RuntimeException("Error while decrypting", e);
            return null;
        }
    }

    // Método auxiliar para obtener una llave secreta desde una llave privada
    private static SecretKey getKeyFromPrivateKey(String privateKey) {
        try {
            byte[] keyBytes = privateKey.getBytes("UTF-8");
            byte[] key = new byte[16];
            System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, key.length));
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error while generating secret key from private key", e);
        }
    }
}
