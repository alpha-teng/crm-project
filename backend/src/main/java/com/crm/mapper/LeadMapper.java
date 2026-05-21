package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.Lead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LeadMapper extends BaseMapper<Lead> {
    
    @Select("SELECT * FROM leads WHERE customer_id = #{customerId}")
    List<Lead> findByCustomerId(Long customerId);
    
    @Select("SELECT * FROM leads WHERE status = #{status}")
    List<Lead> findByStatus(String status);
}
