package com.crm.controller;

import com.crm.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {
    
    private final ISystemConfigService systemConfigService;
    
    /** 获取所有配置 */
    @GetMapping
    public ResponseEntity<Map<String, String>> getAllSettings() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }
    
    /** 批量更新配置 */
    @PutMapping
    public ResponseEntity<Map<String, String>> updateSettings(@RequestBody Map<String, String> configs) {
        systemConfigService.setConfigs(configs);
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }
    
    /** 获取单个配置 */
    @GetMapping("/{key}")
    public ResponseEntity<String> getSetting(@PathVariable String key) {
        String value = systemConfigService.getConfig(key);
        return ResponseEntity.ok(value != null ? value : "");
    }
    
    /** 更新单个配置 */
    @PutMapping("/{key}")
    public ResponseEntity<String> updateSetting(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        systemConfigService.setConfig(key, value);
        return ResponseEntity.ok(value);
    }
}
