package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.HealthIndicator;
import com.familymedical.service.HealthIndicatorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health-indicator")
@RequiredArgsConstructor
public class HealthIndicatorController {

    private final HealthIndicatorService healthIndicatorService;

    @PostMapping
    public Result<?> add(@RequestBody HealthIndicator indicator, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        indicator.setUserId(userId);
        healthIndicatorService.addIndicator(indicator);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String indicatorType) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(healthIndicatorService.getIndicatorPage(userId, indicatorType, page, size));
    }

    @GetMapping("/chart")
    public Result<?> chart(HttpServletRequest request,
                           @RequestParam String indicatorType,
                           @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        }
        return Result.success(healthIndicatorService.getByUserAndType(userId, indicatorType));
    }

    @GetMapping("/tips")
    public Result<?> tips(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(healthIndicatorService.getHealthTips(userId));
    }
}
