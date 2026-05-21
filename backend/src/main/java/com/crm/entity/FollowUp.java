package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("follow_ups")
public class FollowUp {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("content")
    private String content;
    
    @TableField("type")
    private String type;
    
    @TableField("next_follow_up_date")
    private LocalDate nextFollowUpDate;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
