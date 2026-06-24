package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.Diagnosis;
import com.familymedical.entity.User;
import com.familymedical.mapper.DiagnosisMapper;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl extends ServiceImpl<DiagnosisMapper, Diagnosis> implements DiagnosisService {

    private final UserMapper userMapper;

    @Override
    public void createDiagnosis(Diagnosis diagnosis) {
        save(diagnosis);
    }

    @Override
    public IPage<Diagnosis> getDiagnosisPage(int page, int size, Long patientId, Long doctorId) {
        LambdaQueryWrapper<Diagnosis> wrapper = new LambdaQueryWrapper<>();
        if (patientId != null) {
            wrapper.eq(Diagnosis::getPatientId, patientId);
        }
        if (doctorId != null) {
            wrapper.eq(Diagnosis::getDoctorId, doctorId);
        }
        wrapper.orderByDesc(Diagnosis::getCreateTime);
        IPage<Diagnosis> pageResult = page(new Page<>(page, size), wrapper);
        fillUserInfo(pageResult.getRecords());
        return pageResult;
    }

    @Override
    public List<Map<String, Object>> getDiagnosisCategories(Long doctorId) {
        List<Diagnosis> diagnoses = lambdaQuery()
                .eq(Diagnosis::getDoctorId, doctorId)
                .isNotNull(Diagnosis::getDiagnosis)
                .list();
        Map<String, Long> categoryCount = new HashMap<>();
        for (Diagnosis d : diagnoses) {
            String diag = d.getDiagnosis();
            if (diag != null && !diag.isEmpty()) {
                categoryCount.merge(diag, 1L, Long::sum);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> e : categoryCount.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", e.getKey());
            m.put("value", e.getValue());
            result.add(m);
        }
        return result;
    }

    private void fillUserInfo(List<Diagnosis> records) {
        if (records.isEmpty()) return;
        Set<Long> userIds = new HashSet<>();
        for (Diagnosis d : records) {
            if (d.getPatientId() != null) userIds.add(d.getPatientId());
            if (d.getDoctorId() != null) userIds.add(d.getDoctorId());
        }
        if (userIds.isEmpty()) return;
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (Diagnosis d : records) {
            User patient = userMap.get(d.getPatientId());
            if (patient != null) {
                d.setPatientName(patient.getRealName());
                d.setPatientAvatar(patient.getAvatar());
            }
            User doctor = userMap.get(d.getDoctorId());
            if (doctor != null) {
                d.setDoctorName(doctor.getRealName());
                d.setDoctorAvatar(doctor.getAvatar());
            }
        }
    }
}
