package com.familymedical.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_indicator")
public class HealthIndicator {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String indicatorType;

    private BigDecimal indicatorValue;

    private String unit;

    private LocalDateTime measureTime;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
