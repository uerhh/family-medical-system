package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("diagnosis")
public class Diagnosis {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String prescription;
    private String advice;
    private LocalDate followUpDate;
    @TableField(exist = false)
    private String patientName;
    @TableField(exist = false)
    private String patientAvatar;
    @TableField(exist = false)
    private String doctorName;
    @TableField(exist = false)
    private String doctorAvatar;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
