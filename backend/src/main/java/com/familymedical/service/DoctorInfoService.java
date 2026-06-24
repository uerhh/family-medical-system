package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.DoctorInfo;

import java.util.Map;

public interface DoctorInfoService extends IService<DoctorInfo> {
    DoctorInfo getByUserId(Long userId);
    void saveOrUpdateDoctor(DoctorInfo doctorInfo);
    IPage<Map<String, Object>> getDoctorPage(int page, int size, String department, String keyword);
    Map<String, Object> getDoctorDetail(Long userId);
}
