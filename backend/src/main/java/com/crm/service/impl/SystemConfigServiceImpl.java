package com.crm.service.impl;

import com.crm.entity.SystemConfig;
import com.crm.mapper.SystemConfigMapper;
import com.crm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigServiceImpl implements ISystemConfigService {
    
    private final SystemConfigMapper systemConfigMapper;
    
    /** 默认配置（当数据库没有时使用） */
    private static final Map<String, String> DEFAULT_CONFIGS = Map.ofEntries(
            Map.entry("embedding.url", "https://api.siliconflow.cn/v1/embeddings"),
            Map.entry("embedding.model", "BAAI/bge-large-zh-v1.5"),
            Map.entry("embedding.apiKey", "sk-qzuyohpgpcburordewzjsiwwiqrxhlabxxzsjgmlneajfeae"),
            Map.entry("embedding.dimensions", "1024"),
            Map.entry("embedding.maxResults", "10"),
            Map.entry("chat.url", "https://api.siliconflow.cn/v1/chat/completions"),
            Map.entry("chat.model", "Qwen/Qwen2.5-7B-Instruct"),
            Map.entry("chat.apiKey", "sk-qzuyohpgpcburordewzjsiwwiqrxhlabxxzsjgmlneajfeae"),
            Map.entry("chat.temperature", "0.7"),
            Map.entry("chat.maxTokens", "1000"),
            Map.entry("search.topK", "5"),
            Map.entry("search.similarityThreshold", "0.6")
    );
    
    @PostConstruct
    public void initDefaults() {
        DEFAULT_CONFIGS.forEach((key, value) -> {
            SystemConfig existing = systemConfigMapper.findByKey(key);
            if (existing == null) {
                SystemConfig config = new SystemConfig();
                config.setConfigKey(key);
                config.setConfigValue(value);
                config.setDescription(getDescription(key));
                config.setCategory(getCategory(key));
                try {
                    systemConfigMapper.upsert(config);
                    log.info("Initialized config: {} = {}", key, value);
                } catch (Exception e) {
                    log.debug("Config {} already exists or error: {}", key, e.getMessage());
                }
            }
        });
    }
    
    @Override
    public Map<String, String> getAllConfigs() {
        Map<String, String> result = new HashMap<>(DEFAULT_CONFIGS);
        systemConfigMapper.selectList(null).forEach(c -> result.put(c.getConfigKey(), c.getConfigValue()));
        return result;
    }
    
    @Override
    public String getConfig(String key) {
        SystemConfig config = systemConfigMapper.findByKey(key);
        return config != null ? config.getConfigValue() : DEFAULT_CONFIGS.get(key);
    }
    
    @Override
    public String getConfig(String key, String defaultValue) {
        String val = getConfig(key);
        return val != null ? val : defaultValue;
    }
    
    @Override
    public void setConfig(String key, String value) {
        SystemConfig config = new SystemConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(getDescription(key));
        config.setCategory(getCategory(key));
        systemConfigMapper.upsert(config);
        log.info("Updated config: {} = {}", key, value);
    }
    
    @Override
    public void setConfigs(Map<String, String> configs) {
        configs.forEach(this::setConfig);
    }
    
    @Override
    public List<SystemConfig> getConfigsByCategory(String category) {
        return systemConfigMapper.selectList(null).stream()
                .filter(c -> category.equals(c.getCategory()))
                .toList();
    }
    
    private String getDescription(String key) {
        return switch (key) {
            case "embedding.url" -> "向量模型 API 地址";
            case "embedding.model" -> "向量模型名称";
            case "embedding.apiKey" -> "向量模型 API Key";
            case "embedding.dimensions" -> "向量维度（只读）";
            case "embedding.maxResults" -> "向量搜索最大返回数";
            case "chat.url" -> "大模型 API 地址";
            case "chat.model" -> "大模型名称";
            case "chat.apiKey" -> "大模型 API Key";
            case "chat.temperature" -> "温度参数 (0-1)";
            case "chat.maxTokens" -> "最大输出 Token 数";
            case "search.topK" -> "语义搜索返回的最相关文档数";
            case "search.similarityThreshold" -> "相似度阈值";
            default -> key;
        };
    }
    
    private String getCategory(String key) {
        if (key.startsWith("embedding")) return "embedding";
        if (key.startsWith("chat")) return "chat";
        if (key.startsWith("search")) return "search";
        return "other";
    }
}
