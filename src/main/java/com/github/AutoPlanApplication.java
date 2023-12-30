package com.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AutoPlanApplication {
    private static String[] args;
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        AutoPlanApplication.args = args;
        AutoPlanApplication.context = SpringApplication.run(AutoPlanApplication.class, args);
    }

    public static void restart() {
        context.close();
        main(args);
    }

}
