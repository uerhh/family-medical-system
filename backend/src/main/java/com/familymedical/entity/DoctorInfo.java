package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("doctor_info")
public class DoctorInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String department;
    private String title;
    private String specialty;
    private String introduction;
    private BigDecimal consultFee;
    private Integer workYears;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
