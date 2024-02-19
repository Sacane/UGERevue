package fr.pentagon.ugeoverflow.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class CustomPasswordEncoder implements PasswordEncoder {

    private static final String ENCODING = "SHA-256";
    private static final int ENCODING_LENGTH = 64; //Encoding => 32 bytes | Hexadecimal conversion => 32*2 = 64


    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        var random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            var salt = generateSalt();
            var md = MessageDigest.getInstance(ENCODING);
            var hashPassword = md.digest((rawPassword + new String(salt)).getBytes(StandardCharsets.UTF_8));
            var builder = new StringBuilder();
            for (var b : hashPassword) { //Conversion into hexadecimal for a better readability and manipulation.
                builder.append(String.format("%02x", b));
            }
            return builder + new String(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 algorithm doesn't exists anymore");
        }
    }

    public String encodePassword(String password){
        try {
            var salt = generateSalt();
            var md = MessageDigest.getInstance(ENCODING);
            var hashPassword = md.digest((password + new String(salt)).getBytes(StandardCharsets.UTF_8));
            var builder = new StringBuilder();
            for (var b : hashPassword) { //Conversion into hexadecimal for a better readability and manipulation.
                builder.append(String.format("%02x", b));
            }
            return builder + new String(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 algorithm doesn't exists anymore");
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        var salt = encodedPassword.substring(ENCODING_LENGTH);
        try {
            var md = MessageDigest.getInstance(ENCODING);
            var enteredPwdHash = md.digest((rawPassword + salt).getBytes(StandardCharsets.UTF_8));
            var builder = new StringBuilder();
            for (var b : enteredPwdHash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString().equals(encodedPassword.substring(0,64));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 algorithm doesn't exists anymore");
        }
    }

    public boolean verifyPassword(String enteredPwd, String storedPwd){
        var salt = storedPwd.substring(ENCODING_LENGTH);
        try {
            var md = MessageDigest.getInstance(ENCODING);
            var enteredPwdHash = md.digest((enteredPwd + salt).getBytes(StandardCharsets.UTF_8));
            var builder = new StringBuilder();
            for (var b : enteredPwdHash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString().equals(storedPwd.substring(0,64));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-256 algorithm doesn't exists anymore");
        }
    }
}
