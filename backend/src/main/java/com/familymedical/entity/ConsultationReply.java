package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation_reply")
public class ConsultationReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long consultationId;

    private Long senderId;

    private String content;

    private String images;

    @TableField(exist = false)
    private String senderName;

    @TableField(exist = false)
    private String senderAvatar;

    @TableField(exist = false)
    private Integer senderRole;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
