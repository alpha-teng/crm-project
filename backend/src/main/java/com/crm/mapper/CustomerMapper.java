package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    
    @Select("SELECT * FROM customers WHERE status = #{status}")
    List<Customer> findByStatus(String status);
    
    @Select("SELECT * FROM customers WHERE company ILIKE '%' || #{company} || '%'")
    List<Customer> findByCompanyContainingIgnoreCase(String company);
    
    @Select("SELECT * FROM customers WHERE name ILIKE '%' || #{name} || '%'")
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    @Select("SELECT * FROM customers WHERE name ILIKE '%' || #{keyword} || '%' OR company ILIKE '%' || #{keyword} || '%'")
    List<Customer> findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(String keyword, String keyword2);
}
