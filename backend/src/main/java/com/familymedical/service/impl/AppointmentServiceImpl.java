package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.Appointment;
import com.familymedical.entity.DoctorInfo;
import com.familymedical.entity.User;
import com.familymedical.mapper.AppointmentMapper;
import com.familymedical.mapper.DoctorInfoMapper;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    private final UserMapper userMapper;
    private final DoctorInfoMapper doctorInfoMapper;

    @Override
    public void createAppointment(Appointment appointment) {
        appointment.setStatus(0);
        save(appointment);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        lambdaUpdate()
                .eq(Appointment::getId, id)
                .set(Appointment::getStatus, status)
                .update();
    }

    @Override
    public IPage<Appointment> getAppointmentPage(int page, int size, Long patientId, Long doctorId, Integer status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        if (patientId != null) {
            wrapper.eq(Appointment::getPatientId, patientId);
        }
        if (doctorId != null) {
            wrapper.eq(Appointment::getDoctorId, doctorId);
        }
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        wrapper.orderByDesc(Appointment::getCreateTime);
        IPage<Appointment> pageResult = page(new Page<>(page, size), wrapper);
        fillUserInfo(pageResult.getRecords());
        return pageResult;
    }

    @Override
    public List<Map<String, Object>> getAppointmentTrend() {
        return baseMapper.selectMaps(
                new QueryWrapper<Appointment>()
                        .select("DATE_FORMAT(appointment_date, '%Y-%m') as month", "COUNT(*) as count")
                        .groupBy("DATE_FORMAT(appointment_date, '%Y-%m')")
                        .orderByAsc("month"));
    }

    @Override
    public List<Map<String, Object>> getDepartmentDistribution() {
        List<Appointment> all = list();
        if (all.isEmpty()) return List.of();

        Set<Long> doctorIds = all.stream()
                .map(Appointment::getDoctorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, DoctorInfo> doctorMap = doctorInfoMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(DoctorInfo::getUserId, d -> d));

        Map<String, Long> deptCount = new HashMap<>();
        for (Appointment a : all) {
            DoctorInfo info = doctorMap.get(a.getDoctorId());
            String dept = info != null ? info.getDepartment() : "未知";
            deptCount.merge(dept, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> e : deptCount.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", e.getKey());
            m.put("value", e.getValue());
            result.add(m);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDoctorWorkload(int limit) {
        List<Map<String, Object>> result = baseMapper.selectMaps(
                new QueryWrapper<Appointment>()
                        .select("doctor_id as doctorId", "COUNT(*) as count")
                        .groupBy("doctor_id")
                        .orderByDesc("count")
                        .last("LIMIT " + limit));
        Set<Long> doctorIds = result.stream()
                .map(m -> Long.valueOf(m.get("doctorId").toString()))
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (Map<String, Object> m : result) {
            Long docId = Long.valueOf(m.get("doctorId").toString());
            User doctor = userMap.get(docId);
            m.put("name", doctor != null ? doctor.getRealName() : "未知");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDoctorPersonalTrend(Long doctorId) {
        return baseMapper.selectMaps(
                new QueryWrapper<Appointment>()
                        .select("DATE_FORMAT(appointment_date, '%Y-%m') as month", "COUNT(*) as count")
                        .eq("doctor_id", doctorId)
                        .groupBy("DATE_FORMAT(appointment_date, '%Y-%m')")
                        .orderByAsc("month"));
    }

    @Override
    public List<Map<String, Object>> getPatientDemographics(Long doctorId) {
        List<Appointment> appointments = lambdaQuery()
                .eq(Appointment::getDoctorId, doctorId)
                .list();
        if (appointments.isEmpty()) return List.of();

        Set<Long> patientIds = appointments.stream()
                .map(Appointment::getPatientId)
                .collect(Collectors.toSet());
        Map<Long, User> patientMap = userMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<String, Long> genderCount = new HashMap<>();
        Map<String, Long> ageGroupCount = new HashMap<>();
        for (Long pid : patientIds) {
            User patient = patientMap.get(pid);
            if (patient == null) continue;
            String gender = patient.getGender() == 1 ? "男" : (patient.getGender() == 2 ? "女" : "未知");
            genderCount.merge(gender, 1L, Long::sum);
            if (patient.getAge() != null) {
                String ageGroup;
                int age = patient.getAge();
                if (age < 18) ageGroup = "0-17";
                else if (age < 30) ageGroup = "18-29";
                else if (age < 45) ageGroup = "30-44";
                else if (age < 60) ageGroup = "45-59";
                else ageGroup = "60+";
                ageGroupCount.merge(ageGroup, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> genderData = new ArrayList<>();
        for (Map.Entry<String, Long> e : genderCount.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", e.getKey());
            m.put("value", e.getValue());
            genderData.add(m);
        }
        Map<String, Object> genderMap = new HashMap<>();
        genderMap.put("type", "gender");
        genderMap.put("data", genderData);
        result.add(genderMap);

        List<Map<String, Object>> ageData = new ArrayList<>();
        for (Map.Entry<String, Long> e : ageGroupCount.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", e.getKey());
            m.put("value", e.getValue());
            ageData.add(m);
        }
        Map<String, Object> ageMap = new HashMap<>();
        ageMap.put("type", "age");
        ageMap.put("data", ageData);
        result.add(ageMap);
        return result;
    }

    private void fillUserInfo(List<Appointment> records) {
        if (records.isEmpty()) return;
        Set<Long> userIds = new HashSet<>();
        for (Appointment a : records) {
            if (a.getPatientId() != null) userIds.add(a.getPatientId());
            if (a.getDoctorId() != null) userIds.add(a.getDoctorId());
        }
        if (userIds.isEmpty()) return;
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (Appointment a : records) {
            User patient = userMap.get(a.getPatientId());
            if (patient != null) {
                a.setPatientName(patient.getRealName());
                a.setPatientAvatar(patient.getAvatar());
            }
            User doctor = userMap.get(a.getDoctorId());
            if (doctor != null) {
                a.setDoctorName(doctor.getRealName());
                a.setDoctorAvatar(doctor.getAvatar());
            }
        }
    }
}
