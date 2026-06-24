package com.familymedical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.familymedical.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
