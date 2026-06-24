package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.Diagnosis;

import java.util.List;
import java.util.Map;

public interface DiagnosisService extends IService<Diagnosis> {
    void createDiagnosis(Diagnosis diagnosis);
    IPage<Diagnosis> getDiagnosisPage(int page, int size, Long patientId, Long doctorId);

    List<Map<String, Object>> getDiagnosisCategories(Long doctorId);
}
