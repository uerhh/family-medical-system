package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.Consultation;
import com.familymedical.entity.ConsultationReply;

public interface ConsultationService extends IService<Consultation> {

    void createConsultation(Consultation consultation);

    void replyConsultation(Long consultationId, ConsultationReply reply);

    void closeConsultation(Long id);

    IPage<Consultation> getConsultationPage(int page, int size, Long patientId, Long doctorId, Integer status);

    Consultation getDetail(Long id);
}
