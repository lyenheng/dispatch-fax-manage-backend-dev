package com.kedacom.avcs.fax.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kedacom
 * @date 2018-11-16
 */
@SpringBootApplication(scanBasePackages = {"com.kedacom.avcs"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

