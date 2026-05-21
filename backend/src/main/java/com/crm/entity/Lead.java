package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("leads")
public class Lead {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("customer_id")
    private Long customerId;
    
    @TableField("source")
    private String source;
    
    @TableField("status")
    private String status;
    
    @TableField("score")
    private Integer score;
    
    @TableField("remark")
    private String remark;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
