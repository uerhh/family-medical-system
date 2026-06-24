package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.HealthRecord;
import com.familymedical.service.HealthRecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-record")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @GetMapping
    public Result<?> getMyRecord(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(healthRecordService.getByUserId(userId));
    }

    @GetMapping("/{userId}")
    public Result<?> getByUserId(@PathVariable Long userId) {
        return Result.success(healthRecordService.getByUserId(userId));
    }

    @PostMapping
    public Result<?> saveOrUpdate(@RequestBody HealthRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        record.setUserId(userId);
        healthRecordService.saveOrUpdateRecord(record);
        return Result.success();
    }
}
