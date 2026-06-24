package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.HealthIndicator;
import com.familymedical.entity.Notification;
import com.familymedical.mapper.HealthIndicatorMapper;
import com.familymedical.service.HealthIndicatorService;
import com.familymedical.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HealthIndicatorServiceImpl extends ServiceImpl<HealthIndicatorMapper, HealthIndicator>
        implements HealthIndicatorService {

    private final NotificationService notificationService;

    @Override
    public void addIndicator(HealthIndicator indicator) {
        save(indicator);
        checkAndNotifyAbnormal(indicator);
    }

    @Override
    public IPage<HealthIndicator> getIndicatorPage(Long userId, String indicatorType, int page, int size) {
        LambdaQueryWrapper<HealthIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthIndicator::getUserId, userId);
        if (indicatorType != null && !indicatorType.isEmpty()) {
            wrapper.eq(HealthIndicator::getIndicatorType, indicatorType);
        }
        wrapper.orderByDesc(HealthIndicator::getMeasureTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<HealthIndicator> getByUserAndType(Long userId, String indicatorType) {
        return lambdaQuery()
                .eq(HealthIndicator::getUserId, userId)
                .eq(HealthIndicator::getIndicatorType, indicatorType)
                .orderByAsc(HealthIndicator::getMeasureTime)
                .list();
    }

    @Override
    public List<Map<String, Object>> getHealthTips(Long userId) {
        List<Map<String, Object>> tips = new ArrayList<>();

        HealthIndicator latestWeight = getLatestByType(userId, "weight");
        HealthIndicator latestHeight = getLatestByType(userId, "height");
        if (latestWeight != null && latestHeight != null) {
            BigDecimal heightM = latestHeight.getIndicatorValue()
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = latestWeight.getIndicatorValue()
                    .divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
            if (bmi.compareTo(new BigDecimal("24")) > 0) {
                tips.add(Map.of("level", "warning", "message",
                        "您的BMI为" + bmi + "，属于超重范围，建议控制饮食并增加运动"));
            } else if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                tips.add(Map.of("level", "warning", "message",
                        "您的BMI为" + bmi + "，属于偏瘦范围，建议增加营养摄入"));
            } else {
                tips.add(Map.of("level", "success", "message",
                        "您的BMI为" + bmi + "，属于正常范围"));
            }
        }

        HealthIndicator systolic = getLatestByType(userId, "blood_pressure_systolic");
        HealthIndicator diastolic = getLatestByType(userId, "blood_pressure_diastolic");
        if (systolic != null && diastolic != null) {
            if (systolic.getIndicatorValue().compareTo(new BigDecimal("140")) >= 0
                    || diastolic.getIndicatorValue().compareTo(new BigDecimal("90")) >= 0) {
                tips.add(Map.of("level", "danger", "message",
                        "您的血压偏高（" + systolic.getIndicatorValue() + "/" + diastolic.getIndicatorValue()
                                + " mmHg），建议就医检查"));
            }
        }

        HealthIndicator bloodSugar = getLatestByType(userId, "blood_sugar");
        if (bloodSugar != null) {
            if (bloodSugar.getIndicatorValue().compareTo(new BigDecimal("6.1")) >= 0) {
                tips.add(Map.of("level", "warning", "message",
                        "您的空腹血糖为" + bloodSugar.getIndicatorValue() + " mmol/L，偏高，建议控制糖分摄入"));
            }
        }

        HealthIndicator heartRate = getLatestByType(userId, "heart_rate");
        if (heartRate != null) {
            if (heartRate.getIndicatorValue().compareTo(new BigDecimal("100")) > 0) {
                tips.add(Map.of("level", "warning", "message",
                        "您的心率为" + heartRate.getIndicatorValue() + " bpm，偏快"));
            } else if (heartRate.getIndicatorValue().compareTo(new BigDecimal("60")) < 0) {
                tips.add(Map.of("level", "warning", "message",
                        "您的心率为" + heartRate.getIndicatorValue() + " bpm，偏慢"));
            }
        }

        if (tips.isEmpty()) {
            tips.add(Map.of("level", "success", "message", "暂无异常指标，继续保持良好的生活习惯"));
        }
        return tips;
    }

    private HealthIndicator getLatestByType(Long userId, String type) {
        return lambdaQuery()
                .eq(HealthIndicator::getUserId, userId)
                .eq(HealthIndicator::getIndicatorType, type)
                .orderByDesc(HealthIndicator::getMeasureTime)
                .last("LIMIT 1")
                .one();
    }

    private void checkAndNotifyAbnormal(HealthIndicator indicator) {
        List<Map<String, Object>> tips = getHealthTips(indicator.getUserId());
        for (Map<String, Object> tip : tips) {
            if ("danger".equals(tip.get("level")) || "warning".equals(tip.get("level"))) {
                Notification notification = new Notification();
                notification.setUserId(indicator.getUserId());
                notification.setTitle("健康指标预警");
                notification.setContent((String) tip.get("message"));
                notification.setType(1);
                notificationService.createNotification(notification);
            }
        }
    }
}
