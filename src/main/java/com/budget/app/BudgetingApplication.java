package com.budget.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BudgetingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BudgetingApplication.class, args);
        System.out.println("Budgeting App is running at http://localhost:8080");
    }
}
