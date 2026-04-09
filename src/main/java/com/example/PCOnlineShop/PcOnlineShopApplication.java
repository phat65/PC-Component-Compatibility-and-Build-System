package com.example.PCOnlineShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class PcOnlineShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcOnlineShopApplication.class, args);
    }
}
