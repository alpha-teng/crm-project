package com.crm.util;

import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库导出工具：导出表结构和数据
 * 使用方法：mvn exec:java -Dexec.mainClass="com.crm.util.DatabaseExporter"
 */
public class DatabaseExporter {

    private static final String OUTPUT_FILE = "src/main/resources/db/init.sql";

    public static void main(String[] args) {
        String url = "jdbc:postgresql://192.168.1.160:5432/crm";
        String user = "postgres";
        String password = "Afa123456@";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             FileWriter writer = new FileWriter(OUTPUT_FILE)) {

            writer.write("-- CRM Database Export\n");
            writer.write("-- Generated: " + new Timestamp(System.currentTimeMillis()) + "\n\n");
            writer.write("-- Enable pgvector extension\n");
            writer.write("CREATE EXTENSION IF NOT EXISTS vector;\n\n");

            DatabaseMetaData metaData = conn.getMetaData();

            // 1. 获取所有表
            ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            List<String> tableNames = new ArrayList<>();

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (!tableName.startsWith("flyway") && !tableName.equals("databasechangelog") && !tableName.equals("databasechangeloglock")) {
                    tableNames.add(tableName);
                }
            }

            // 2. 导出表结构
            writer.write("-- ==================== TABLE STRUCTURES ====================\n\n");
            for (String tableName : tableNames) {
                exportTableStructure(conn, tableName, writer);
            }

            // 3. 导出数据
            writer.write("\n-- ==================== TABLE DATA ====================\n\n");
            for (String tableName : tableNames) {
                exportTableData(conn, tableName, writer);
            }

            writer.write("\n-- Export completed successfully!\n");
            System.out.println("✅ 数据库导出成功: " + OUTPUT_FILE);

        } catch (Exception e) {
            System.err.println("❌ 导出失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void exportTableStructure(Connection conn, String tableName, FileWriter writer) throws Exception {
        writer.write("-- Table: " + tableName + "\n");
        writer.write("DROP TABLE IF EXISTS " + tableName + " CASCADE;\n");

        // 获取建表语句（通过查询表定义）
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT column_name, data_type, is_nullable, column_default, character_maximum_length " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = ? AND table_schema = 'public' " +
                        "ORDER BY ordinal_position"
        );
        pstmt.setString(1, tableName);
        ResultSet columns = pstmt.executeQuery();

        writer.write("CREATE TABLE " + tableName + " (\n");

        List<String> columnDefs = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        ResultSet pkRs = conn.getMetaData().getPrimaryKeys(null, "public", tableName);
        while (pkRs.next()) {
            primaryKeys.add(pkRs.getString("COLUMN_NAME"));
        }

        while (columns.next()) {
            String colName = columns.getString("column_name");
            String dataType = columns.getString("data_type");
            String isNullable = columns.getString("is_nullable");
            String defaultValue = columns.getString("column_default");
            String maxLength = columns.getString("character_maximum_length");

            // 处理向量类型
            if (dataType.equals("USER-DEFINED")) {
                dataType = "vector(1024)"; // 假设是 vector 类型
            }

            StringBuilder colDef = new StringBuilder("    " + colName + " " + dataType);

            if (maxLength != null) {
                colDef.append("(").append(maxLength).append(")");
            }

            if ("NO".equals(isNullable)) {
                colDef.append(" NOT NULL");
            }

            if (defaultValue != null && !defaultValue.contains("nextval")) {
                colDef.append(" DEFAULT ").append(defaultValue);
            } else if (defaultValue != null) {
                colDef.append(" DEFAULT ").append(defaultValue);
            }

            columnDefs.add(colDef.toString());
        }

        // 添加主键约束
        if (!primaryKeys.isEmpty()) {
            columnDefs.add("    PRIMARY KEY (" + String.join(", ", primaryKeys) + ")");
        }

        writer.write(String.join(",\n", columnDefs) + "\n");
        writer.write(");\n\n");

        columns.close();
        pstmt.close();
    }

    private static void exportTableData(Connection conn, String tableName, FileWriter writer) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet data = stmt.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData md = data.getMetaData();
        int columnCount = md.getColumnCount();

        writer.write("-- Data for table: " + tableName + "\n");

        int rowCount = 0;
        while (data.next()) {
            if (rowCount == 0) {
                writer.write("INSERT INTO " + tableName + " (");
                List<String> columnNames = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(md.getColumnName(i));
                }
                writer.write(String.join(", ", columnNames) + ") VALUES\n");
            }

            writer.write(rowCount == 0 ? "  (" : ", (");

            List<String> values = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                Object value = data.getObject(i);
                if (value == null) {
                    values.add("NULL");
                } else if (value instanceof String || value instanceof Timestamp || value instanceof java.sql.Date) {
                    values.add("'" + value.toString().replace("'", "''") + "'");
                } else if (value instanceof java.sql.Array) {
                    values.add("'" + ((java.sql.Array) value).toString() + "'");
                } else {
                    values.add(value.toString());
                }
            }

            writer.write(String.join(", ", values) + ")\n");
            rowCount++;
        }

        if (rowCount > 0) {
            writer.write(";\n");
        }
        writer.write("-- " + rowCount + " rows exported\n\n");

        data.close();
        stmt.close();
    }
}
