package com.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
    
    @Select("SELECT * FROM system_config WHERE config_key = #{key}")
    SystemConfig findByKey(@Param("key") String key);
    
    @Insert("INSERT INTO system_config (config_key, config_value, description, category) " +
            "VALUES (#{configKey}, #{configValue}, #{description}, #{category}) " +
            "ON CONFLICT (config_key) DO UPDATE SET config_value = EXCLUDED.config_value")
    void upsert(SystemConfig config);
}
