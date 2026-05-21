package com.crm.service;

import com.crm.entity.SystemConfig;
import java.util.List;
import java.util.Map;

public interface ISystemConfigService {
    
    Map<String, String> getAllConfigs();
    
    String getConfig(String key);
    
    String getConfig(String key, String defaultValue);
    
    void setConfig(String key, String value);
    
    void setConfigs(Map<String, String> configs);
    
    List<SystemConfig> getConfigsByCategory(String category);
}
