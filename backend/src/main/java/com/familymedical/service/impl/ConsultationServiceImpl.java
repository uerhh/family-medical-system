package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.Consultation;
import com.familymedical.entity.ConsultationReply;
import com.familymedical.entity.User;
import com.familymedical.mapper.ConsultationMapper;
import com.familymedical.mapper.ConsultationReplyMapper;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation>
        implements ConsultationService {

    private final ConsultationReplyMapper consultationReplyMapper;
    private final UserMapper userMapper;

    @Override
    public void createConsultation(Consultation consultation) {
        consultation.setStatus(0);
        consultation.setDoctorId(null);
        save(consultation);
    }

    @Override
    public void replyConsultation(Long consultationId, ConsultationReply reply) {
        reply.setConsultationId(consultationId);
        consultationReplyMapper.insert(reply);

        Consultation consultation = getById(consultationId);
        if (consultation != null) {
            if (consultation.getDoctorId() == null) {
                consultation.setDoctorId(reply.getSenderId());
            }
            consultation.setStatus(1);
            updateById(consultation);
        }
    }

    @Override
    public void closeConsultation(Long id) {
        lambdaUpdate().eq(Consultation::getId, id).set(Consultation::getStatus, 2).update();
    }

    @Override
    public IPage<Consultation> getConsultationPage(int page, int size, Long patientId, Long doctorId, Integer status) {
        LambdaQueryWrapper<Consultation> wrapper = new LambdaQueryWrapper<>();
        if (patientId != null) {
            wrapper.eq(Consultation::getPatientId, patientId);
        }
        if (doctorId != null) {
            wrapper.eq(Consultation::getDoctorId, doctorId);
        }
        if (status != null) {
            wrapper.eq(Consultation::getStatus, status);
        }
        wrapper.orderByDesc(Consultation::getCreateTime);
        IPage<Consultation> result = page(new Page<>(page, size), wrapper);
        fillConsultationUserInfo(result.getRecords());
        return result;
    }

    @Override
    public Consultation getDetail(Long id) {
        Consultation consultation = getById(id);
        if (consultation == null) {
            return null;
        }

        List<ConsultationReply> replies = consultationReplyMapper.selectList(
                new LambdaQueryWrapper<ConsultationReply>()
                        .eq(ConsultationReply::getConsultationId, id)
                        .orderByAsc(ConsultationReply::getCreateTime));
        fillReplyUserInfo(replies);
        consultation.setReplies(replies);

        fillSingleConsultationUserInfo(consultation);
        return consultation;
    }

    private void fillConsultationUserInfo(List<Consultation> list) {
        if (list.isEmpty()) return;
        Set<Long> userIds = new HashSet<>();
        for (Consultation c : list) {
            userIds.add(c.getPatientId());
            if (c.getDoctorId() != null) userIds.add(c.getDoctorId());
        }
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (Consultation c : list) {
            User patient = userMap.get(c.getPatientId());
            if (patient != null) {
                c.setPatientName(patient.getRealName());
                c.setPatientAvatar(patient.getAvatar());
            }
            if (c.getDoctorId() != null) {
                User doctor = userMap.get(c.getDoctorId());
                if (doctor != null) {
                    c.setDoctorName(doctor.getRealName());
                    c.setDoctorAvatar(doctor.getAvatar());
                }
            }
        }
    }

    private void fillSingleConsultationUserInfo(Consultation c) {
        User patient = userMapper.selectById(c.getPatientId());
        if (patient != null) {
            c.setPatientName(patient.getRealName());
            c.setPatientAvatar(patient.getAvatar());
        }
        if (c.getDoctorId() != null) {
            User doctor = userMapper.selectById(c.getDoctorId());
            if (doctor != null) {
                c.setDoctorName(doctor.getRealName());
                c.setDoctorAvatar(doctor.getAvatar());
            }
        }
    }

    private void fillReplyUserInfo(List<ConsultationReply> replies) {
        if (replies.isEmpty()) return;
        Set<Long> senderIds = replies.stream()
                .map(ConsultationReply::getSenderId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(senderIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (ConsultationReply reply : replies) {
            User sender = userMap.get(reply.getSenderId());
            if (sender != null) {
                reply.setSenderName(sender.getRealName());
                reply.setSenderAvatar(sender.getAvatar());
                reply.setSenderRole(sender.getRole());
            }
        }
    }
}
