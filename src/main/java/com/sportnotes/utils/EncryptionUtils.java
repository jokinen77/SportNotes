/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.Random;

/**
 *
 * @author jaakko
 */
public class EncryptionUtils {
    
    private static final int saltLength = 20;
    private static final int tokenLength = 128;

    public static String hashPassword(String password) {
        String salt = generateSalt();
        return salt + hashSha512(salt + password);
    }
    
    public static boolean passwordMatchHash(String password, String hash) {
        String salt = hash.substring(0, saltLength);
        String saltAndHash = salt + hashSha512(salt + password);
        return hash.equals(saltAndHash);
    }
    
    public static String generateToken() {
        return generateString(tokenLength);
    }
    
    private static String hashSha512(String text) {
        HashFunction hf = Hashing.sha512();
        HashCode hc = hf.newHasher().putString(text, Charsets.UTF_8).hash();
        return hc.toString();
    }
    
    private static String generateString(int length) {
        Random random = new Random();
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String all = upper + lower + numbers;
        String salt = new String();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(all.length());
            salt += all.charAt(index);
        }
        return salt;
    }

    private static String generateSalt() {
        return generateString(saltLength);
    }
    
    
    
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String salt = generateSalt();
            System.out.println("Salt: " + salt + ", length: " + salt.length());
        }
        for (int i = 0; i < 10; i++) {
            String password = generateSalt() + generateSalt();
            String hash = hashPassword(password);
            System.out.println("Password: " + password + ", match: " + passwordMatchHash(password, hash) + ", password lenth: " + hash.length());
        }
        for (int i = 0; i < 10; i++) {
            String token = generateToken();
            System.out.println("Token: " + token + ", length: " + token.length());
        }
    }

}
