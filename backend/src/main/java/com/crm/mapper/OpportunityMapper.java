package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.Opportunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OpportunityMapper extends BaseMapper<Opportunity> {
    
    @Select("SELECT * FROM opportunities WHERE customer_id = #{customerId}")
    List<Opportunity> findByCustomerId(Long customerId);
    
    @Select("SELECT * FROM opportunities WHERE stage = #{stage}")
    List<Opportunity> findByStage(String stage);
}
