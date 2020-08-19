package com.cj;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.cj")

public class RandomApplication extends SpringBootServletInitializer  {

	public static void main(String[] args)  throws Exception{
		SpringApplication.run(RandomApplication.class, args);
	}
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RandomApplication.class);
    }
}
