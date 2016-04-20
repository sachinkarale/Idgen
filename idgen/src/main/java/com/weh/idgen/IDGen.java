package com.weh.idgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.weh.idgen.controller.IDGenController;

/**
 * IDGen<br>
 * initialize the start process of IDGen service. 
 * and starts the IDGenController class,<br>
 * when initializing the process.
 * @author BizRuntime
 */

@SpringBootApplication
@Configuration
@ComponentScan(basePackageClasses = IDGenController.class)
public class IDGen {
	public static void main(String[] args) {
		SpringApplication.run(IDGen.class, args);
	}
}