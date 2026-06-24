package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.HealthIndicator;

import java.util.List;
import java.util.Map;

public interface HealthIndicatorService extends IService<HealthIndicator> {

    void addIndicator(HealthIndicator indicator);

    IPage<HealthIndicator> getIndicatorPage(Long userId, String indicatorType, int page, int size);

    List<HealthIndicator> getByUserAndType(Long userId, String indicatorType);

    List<Map<String, Object>> getHealthTips(Long userId);
}
