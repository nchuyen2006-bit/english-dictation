package com.java.utils;

import java.util.Base64;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.java.repository.entity.UserEntity;

public class JwtUtil {
    
    private static final String SECRET_KEY = "your-secret-key-change-in-production-min-256-bits";
    private static final long EXPIRATION_TIME = 86400000; // 24 giờ

    /**
     * Tạo JWT token đơn giản (không dùng thư viện)
     */
    public static String generateToken(UserEntity user) {
        try {
            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + EXPIRATION_TIME;
            
            // Tạo header
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes());
            
            // Tạo payload
            String payload = String.format(
                "{\"sub\":\"%s\",\"id\":%d,\"role\":\"%s\",\"exp\":%d}",
                user.getEmail(), user.getId(), user.getRole(), expMillis
            );
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes());
            
            // Tạo signature
            String data = encodedHeader + "." + encodedPayload;
            String signature = createSignature(data);
            
            return data + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * Xác thực token
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // Kiểm tra signature
            String data = parts[0] + "." + parts[1];
            String signature = createSignature(data);
            
            if (!signature.equals(parts[2])) {
                return false;
            }
            
            // Kiểm tra expiration
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            long exp = extractExpiration(payload);
            
            return exp > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy email từ token
     */
    public static String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse JSON đơn giản
            int subStart = payload.indexOf("\"sub\":\"") + 7;
            int subEnd = payload.indexOf("\"", subStart);
            
            return payload.substring(subStart, subEnd);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lấy role từ token
     */
    public static String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            int roleStart = payload.indexOf("\"role\":\"") + 8;
            int roleEnd = payload.indexOf("\"", roleStart);
            
            return payload.substring(roleStart, roleEnd);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tạo signature bằng HMAC-SHA256
     */
    private static String createSignature(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    /**
     * Lấy thời gian hết hạn từ payload
     */
    private static long extractExpiration(String payload) {
        int expStart = payload.indexOf("\"exp\":") + 6;
        int expEnd = payload.indexOf("}", expStart);
        String expStr = payload.substring(expStart, expEnd).trim();
        
        return Long.parseLong(expStr);
    }
}
