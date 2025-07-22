package com.example.vpn_bot;

import com.example.vpn_bot.config.CryptoWalletConfig;
import com.example.vpn_bot.telegram.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties({TelegramProperties.class, CryptoWalletConfig.class})
@EnableJpaRepositories(basePackages = "com.example.vpn_bot.repository")
@EntityScan("com.example.vpn_bot.entity") // Сканирование сущностей
@EnableAspectJAutoProxy
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
