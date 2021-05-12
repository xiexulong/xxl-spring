package com.xxl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args ) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(App.class, args);
    }
}
