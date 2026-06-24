package com.familymedical.controller;

import com.familymedical.common.Result;
import com.familymedical.entity.Diagnosis;
import com.familymedical.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public Result<?> create(@RequestBody Diagnosis diagnosis) {
        diagnosisService.createDiagnosis(diagnosis);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId) {
        return Result.success(diagnosisService.getDiagnosisPage(page, size, patientId, doctorId));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(diagnosisService.getById(id));
    }
}
