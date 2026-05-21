package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("opportunities")
public class Opportunity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("title")
    private String title;
    
    @TableField("amount")
    private BigDecimal amount;
    
    @TableField("stage")
    private String stage;
    
    @TableField("probability")
    private Integer probability;
    
    @TableField("expected_close_date")
    private LocalDate expectedCloseDate;
    
    @TableField("remark")
    private String remark;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
