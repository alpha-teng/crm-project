package com.crm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSchemaFix implements ApplicationRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE leads ALTER COLUMN customer_id DROP NOT NULL");
            log.info("✅ leads.customer_id → nullable");
        } catch (Exception e) {
            log.warn("leads.customer_id already nullable or error: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE deals ALTER COLUMN customer_id DROP NOT NULL");
            log.info("✅ deals.customer_id → nullable");
        } catch (Exception e) {
            log.warn("deals.customer_id already nullable or error: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE opportunities ALTER COLUMN customer_id DROP NOT NULL");
            log.info("✅ opportunities.customer_id → nullable");
        } catch (Exception e) {
            log.warn("opportunities.customer_id already nullable or error: {}", e.getMessage());
        }
        // 知识库表补充 title/category 字段
        try {
            jdbcTemplate.execute("ALTER TABLE knowledge_docs ADD COLUMN IF NOT EXISTS title VARCHAR(255)");
            log.info("✅ knowledge_docs.title added");
        } catch (Exception e) {
            log.warn("knowledge_docs.title: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE knowledge_docs ADD COLUMN IF NOT EXISTS category VARCHAR(100)");
            log.info("✅ knowledge_docs.category added");
        } catch (Exception e) {
            log.warn("knowledge_docs.category: {}", e.getMessage());
        }
        // leads 表补充字段
        for (String col : List.of(
                "ALTER TABLE leads ADD COLUMN IF NOT EXISTS name VARCHAR(100)",
                "ALTER TABLE leads ADD COLUMN IF NOT EXISTS company VARCHAR(200)",
                "ALTER TABLE leads ADD COLUMN IF NOT EXISTS phone VARCHAR(50)",
                "ALTER TABLE leads ADD COLUMN IF NOT EXISTS email VARCHAR(100)")) {
            try { jdbcTemplate.execute(col); log.info("✅ {} done", col.split(" ")[4]); } 
            catch (Exception e) { log.warn("leads col: {}", e.getMessage()); }
        }
        // deals 表补充字段
        try { jdbcTemplate.execute("ALTER TABLE deals ADD COLUMN IF NOT EXISTS title VARCHAR(255)"); log.info("✅ deals.title added"); }
        catch (Exception e) { log.warn("deals.title: {}", e.getMessage()); }
    }
}
