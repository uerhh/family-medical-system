package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.Appointment;
import com.familymedical.entity.User;
import com.familymedical.service.AppointmentService;
import com.familymedical.service.DiagnosisService;
import com.familymedical.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final DiagnosisService diagnosisService;

    @GetMapping("/overview")
    public Result<?> overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.lambdaQuery().eq(User::getRole, Constants.ROLE_USER).count());
        stats.put("doctorCount", userService.lambdaQuery().eq(User::getRole, Constants.ROLE_DOCTOR).count());
        stats.put("todayAppointments", appointmentService.lambdaQuery()
                .eq(Appointment::getAppointmentDate, LocalDate.now()).count());
        stats.put("pendingAppointments", appointmentService.lambdaQuery()
                .eq(Appointment::getStatus, 0).count());
        stats.put("totalAppointments", appointmentService.count());
        stats.put("completedAppointments", appointmentService.lambdaQuery()
                .eq(Appointment::getStatus, 2).count());
        return Result.success(stats);
    }

    @GetMapping("/doctor")
    public Result<?> doctorStats(Long doctorId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayCount", appointmentService.lambdaQuery()
                .eq(Appointment::getDoctorId, doctorId)
                .eq(Appointment::getAppointmentDate, LocalDate.now()).count());
        stats.put("pendingCount", appointmentService.lambdaQuery()
                .eq(Appointment::getDoctorId, doctorId)
                .eq(Appointment::getStatus, 0).count());
        stats.put("confirmedCount", appointmentService.lambdaQuery()
                .eq(Appointment::getDoctorId, doctorId)
                .eq(Appointment::getStatus, 1).count());
        stats.put("completedCount", appointmentService.lambdaQuery()
                .eq(Appointment::getDoctorId, doctorId)
                .eq(Appointment::getStatus, 2).count());
        return Result.success(stats);
    }

    @GetMapping("/user-growth")
    public Result<?> userGrowth() {
        return Result.success(userService.getUserGrowthTrend());
    }

    @GetMapping("/appointment-trend")
    public Result<?> appointmentTrend() {
        return Result.success(appointmentService.getAppointmentTrend());
    }

    @GetMapping("/department-distribution")
    public Result<?> departmentDistribution() {
        return Result.success(appointmentService.getDepartmentDistribution());
    }

    @GetMapping("/doctor-workload")
    public Result<?> doctorWorkload() {
        return Result.success(appointmentService.getDoctorWorkload(10));
    }

    @GetMapping("/doctor-personal")
    public Result<?> doctorPersonal(Long doctorId) {
        return Result.success(appointmentService.getDoctorPersonalTrend(doctorId));
    }

    @GetMapping("/patient-demographics")
    public Result<?> patientDemographics(Long doctorId) {
        return Result.success(appointmentService.getPatientDemographics(doctorId));
    }

    @GetMapping("/diagnosis-categories")
    public Result<?> diagnosisCategories(Long doctorId) {
        return Result.success(diagnosisService.getDiagnosisCategories(doctorId));
    }
}
