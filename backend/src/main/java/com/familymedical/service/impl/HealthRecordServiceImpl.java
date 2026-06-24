package com.familymedical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.HealthRecord;
import com.familymedical.mapper.HealthRecordMapper;
import com.familymedical.service.HealthRecordService;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public HealthRecord getByUserId(Long userId) {
        return lambdaQuery()
                .eq(HealthRecord::getUserId, userId)
                .one();
    }

    @Override
    public void saveOrUpdateRecord(HealthRecord record) {
        HealthRecord existing = getByUserId(record.getUserId());
        if (existing != null) {
            record.setId(existing.getId());
            updateById(record);
        } else {
            save(record);
        }
    }
}
