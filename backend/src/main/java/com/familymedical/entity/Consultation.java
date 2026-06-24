package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("consultation")
public class Consultation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long patientId;

    private Long doctorId;

    private String title;

    private String symptoms;

    private String images;

    private Integer status;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private String patientAvatar;

    @TableField(exist = false)
    private String doctorName;

    @TableField(exist = false)
    private String doctorAvatar;

    @TableField(exist = false)
    private List<ConsultationReply> replies;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
