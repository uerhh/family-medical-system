package com.familymedical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.HealthRecord;

public interface HealthRecordService extends IService<HealthRecord> {
    HealthRecord getByUserId(Long userId);
    void saveOrUpdateRecord(HealthRecord record);
}
