package com.familymedical;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.familymedical.mapper")
public class FamilyMedicalApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyMedicalApplication.class, args);
    }
}
