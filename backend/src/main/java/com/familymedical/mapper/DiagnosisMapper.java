package com.familymedical.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.familymedical.entity.Diagnosis;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiagnosisMapper extends BaseMapper<Diagnosis> {
}
