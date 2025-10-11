package ru.mfa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "ru.mfa.web", "ru.mfa.airline" })
public class MavenDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MavenDemoApplication.class, args);
    }
}
