package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.Consultation;
import com.familymedical.entity.ConsultationReply;
import com.familymedical.service.ConsultationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultation")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public Result<?> create(@RequestBody Consultation consultation, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        consultation.setPatientId(userId);
        consultationService.createConsultation(consultation);
        return Result.success();
    }

    @PostMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Long id, @RequestBody ConsultationReply reply,
                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        reply.setSenderId(userId);
        consultationService.replyConsultation(id, reply);
        return Result.success();
    }

    @PutMapping("/{id}/close")
    public Result<?> close(@PathVariable Long id) {
        consultationService.closeConsultation(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long patientId,
                          @RequestParam(required = false) Long doctorId,
                          @RequestParam(required = false) Integer status) {
        return Result.success(consultationService.getConsultationPage(page, size, patientId, doctorId, status));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(consultationService.getDetail(id));
    }
}
