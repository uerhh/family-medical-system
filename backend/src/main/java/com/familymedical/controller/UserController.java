package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.User;
import com.familymedical.service.OssService;
import com.familymedical.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OssService ossService;

    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(userService.getUserById(userId));
    }

    @PutMapping("/info")
    public Result<?> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        user.setId(userId);
        userService.updateUser(user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        User user = userService.getById(userId);
        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(oldPwd.getBytes()))) {
            return Result.error("原密码错误");
        }
        User update = new User();
        update.setId(userId);
        update.setPassword(DigestUtils.md5DigestAsHex(newPwd.getBytes()));
        userService.updateUser(update);
        return Result.success();
    }

    @PostMapping("/avatar")
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        String url = ossService.uploadAvatar(file);
        User update = new User();
        update.setId(userId);
        update.setAvatar(url);
        userService.updateUser(update);
        return Result.success(url);
    }

    @GetMapping("/batch")
    public Result<?> getUsersByIds(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return Result.success(userService.getUsersByIds(idList));
    }
}
