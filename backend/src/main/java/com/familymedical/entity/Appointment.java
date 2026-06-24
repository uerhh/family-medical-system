package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String symptoms;
    private Integer status;
    private String remark;
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
