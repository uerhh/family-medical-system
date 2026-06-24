package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<?> list(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(notificationService.getByUserId(userId, page, size));
    }

    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }
}
