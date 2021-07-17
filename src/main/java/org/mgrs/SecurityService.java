package org.mgrs;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class SecurityService {
    fileService fileManager;
    static String loggedInUser;

    SecurityService(){
        fileManager = new fileService(1);
    }
    public void setLoginInfo(String username, String txtPassword){
        String hashedString = hashPassword(txtPassword, username);
        fileManager.addLoginData(username, hashedString, "");
    }

    public int authenticateLogin(String username, String txtPassword){
        if(!fileManager.checkUserNameInFile(username)){
            return -1;
        }
        String hashedString = hashPassword(txtPassword, username);
        if(fileManager.checkHashedPasswordsFromFile(hashedString) == 0){
            return -1;
        }
        loggedInUser = username;
        return 1;
    }

    public fileService getFileManager(){
        return fileManager;
    }

    String getLoggedInUser(){
        return loggedInUser;
    }

    public void setLoggedInUser(){ loggedInUser = "";}

    public String hashPassword( final String password, final String salt) {
        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( passwordChars, saltBytes, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return Hex.encodeHexString(res);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    public String encrypt(String password, String key) throws Exception {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        char[] keyBytes = key.toCharArray();
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(keyBytes, salt, 65536, 256);
        SecretKey aesKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] cipherText = cipher.doFinal(passwordBytes);
        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();
        return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
    }

    public String decrypt(String cText, String key) throws Exception {
        String rtnStr = "";
        char[] keyBytes = key.toCharArray();
        byte[] decode = Base64.getDecoder().decode(cText.getBytes(StandardCharsets.UTF_8));
        ByteBuffer bb = ByteBuffer.wrap(decode);
        byte[] iv = new byte[12];
        bb.get(iv);
        byte[] salt = new byte[16];
        bb.get(salt);
        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(keyBytes, salt, 65536, 256);
        SecretKey aesKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        try {
            byte[] plainText = cipher.doFinal(cipherText);
            rtnStr = new String(plainText, StandardCharsets.UTF_8);
        } catch(Exception e){
            rtnStr = "NULL";
        }
        return rtnStr;
    }
}
