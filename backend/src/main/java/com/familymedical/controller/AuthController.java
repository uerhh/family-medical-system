package com.familymedical.controller;

import com.familymedical.common.Result;
import com.familymedical.entity.User;
import com.familymedical.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录接口
     * @param params 包含 username 和 password 的请求参数
     * @return 登录成功返回 JWT token 和用户信息
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = userService.login(username, password);
        return Result.success(result);
    }

    /**
     * 用户注册接口
     * @param params 包含用户名、密码、验证码等注册信息
     * @return 注册成功返回成功结果
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> params) {
        // 验证验证码
        String captchaKey = params.get("captchaKey");
        String captchaCode = params.get("captchaCode");
        if (!CaptchaController.verifyCaptcha(captchaKey, captchaCode)) {
            return Result.error("验证码错误");
        }

        // 构建用户对象并注册
        User user = new User();
        user.setUsername(params.get("username"));
        user.setPassword(params.get("password"));
        user.setRealName(params.get("realName"));
        user.setPhone(params.get("phone"));
        user.setGender(Integer.parseInt(params.get("gender")));
        userService.register(user);
        return Result.success();
    }
}
