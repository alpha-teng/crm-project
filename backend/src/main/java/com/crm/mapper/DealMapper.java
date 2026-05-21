package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.Deal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DealMapper extends BaseMapper<Deal> {
    
    @Select("SELECT * FROM deals WHERE customer_id = #{customerId}")
    List<Deal> findByCustomerId(Long customerId);
    
    @Select("SELECT * FROM deals WHERE status = #{status}")
    List<Deal> findByStatus(String status);
}
