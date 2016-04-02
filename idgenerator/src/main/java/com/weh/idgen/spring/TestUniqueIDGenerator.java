package com.weh.idgen.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.weh.idgen.controller.IDGeneratorController;

/**
 * 
 * Test class for unique ID Generator<br>
 * 
 * @author BizRuntime
 */

@SpringBootApplication
@Configuration
@ComponentScan(basePackageClasses = IDGeneratorController.class)
public class TestUniqueIDGenerator {

	public static void main(String[] args) {

		SpringApplication.run(TestUniqueIDGenerator.class, args);

	}

}