package com.example.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// @Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        String sqlFilePath = "/home/devbox/project/database-schema.sql";
        
        try {
            // Read SQL file
            List<String> sqlStatements = readSqlFile(sqlFilePath);
            
            // Execute SQL statements
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty() && !sql.trim().startsWith("--")) {
                    System.out.println("Executing: " + sql);
                    jdbcTemplate.execute(sql);
                }
            }
            
            System.out.println("Database schema created successfully!");
            
        } catch (Exception e) {
            System.out.println("Error executing SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<String> readSqlFile(String filePath) throws Exception {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comment lines
                if (line.trim().startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line);
                
                // Check if it's the end of a statement
                if (line.trim().endsWith(";")) {
                    sqlStatements.add(sqlBuilder.toString());
                    sqlBuilder.setLength(0);
                }
            }
        }
        
        return sqlStatements;
    }
}