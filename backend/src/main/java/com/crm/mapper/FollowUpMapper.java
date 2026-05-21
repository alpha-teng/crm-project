package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.FollowUp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowUpMapper extends BaseMapper<FollowUp> {
    
    @Select("SELECT * FROM follow_ups WHERE customer_id = #{customerId}")
    List<FollowUp> findByCustomerId(Long customerId);
}
