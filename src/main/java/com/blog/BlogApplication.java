package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        //BlogApplication.class.getClassLoader();
        SpringApplication.run(BlogApplication.class, args);
    }

}

