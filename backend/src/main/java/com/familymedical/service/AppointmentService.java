package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.Appointment;

import java.util.List;
import java.util.Map;

public interface AppointmentService extends IService<Appointment> {
    void createAppointment(Appointment appointment);
    void updateStatus(Long id, Integer status);
    IPage<Appointment> getAppointmentPage(int page, int size, Long patientId, Long doctorId, Integer status);

    List<Map<String, Object>> getAppointmentTrend();

    List<Map<String, Object>> getDepartmentDistribution();

    List<Map<String, Object>> getDoctorWorkload(int limit);

    List<Map<String, Object>> getDoctorPersonalTrend(Long doctorId);

    List<Map<String, Object>> getPatientDemographics(Long doctorId);
}
