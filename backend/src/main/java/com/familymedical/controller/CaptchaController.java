package com.familymedical.controller;

import com.familymedical.common.Result;
import com.familymedical.util.CaptchaUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {

    private static final long CAPTCHA_EXPIRE_MS = 5 * 60 * 1000;

    private static final ConcurrentHashMap<String, CaptchaEntry> CAPTCHA_STORE = new ConcurrentHashMap<>();

    @GetMapping("/captcha")
    public Result<?> captcha() {
        cleanupExpired();
        String code = CaptchaUtil.generateCode();
        String key = UUID.randomUUID().toString().replace("-", "");
        try {
            String base64Image = CaptchaUtil.generateBase64Image(code);
            CAPTCHA_STORE.put(key, new CaptchaEntry(code.toLowerCase(), System.currentTimeMillis()));
            return Result.success(Map.of("key", key, "image", base64Image));
        } catch (Exception e) {
            return Result.error("验证码生成失败");
        }
    }

    public static boolean verifyCaptcha(String key, String code) {
        if (key == null || code == null) return false;
        CaptchaEntry entry = CAPTCHA_STORE.remove(key);
        if (entry == null) return false;
        if (System.currentTimeMillis() - entry.timestamp > CAPTCHA_EXPIRE_MS) return false;
        return entry.code.equals(code.toLowerCase());
    }

    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        CAPTCHA_STORE.entrySet().removeIf(e -> now - e.getValue().timestamp > CAPTCHA_EXPIRE_MS);
    }

    private static class CaptchaEntry {
        String code;
        long timestamp;
        CaptchaEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}
