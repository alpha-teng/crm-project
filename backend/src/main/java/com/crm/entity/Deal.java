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
@TableName("deals")
public class Deal {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("opportunity_id")
    private Long opportunityId;
    
    @TableField("title")
    private String title;
    
    @TableField("amount")
    private BigDecimal amount;
    
    @TableField("contract_date")
    private LocalDate dealDate;
    
    @TableField("status")
    private String status;
    
    @TableField("remark")
    private String remark;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
