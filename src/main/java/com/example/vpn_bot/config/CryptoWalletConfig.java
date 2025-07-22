package com.example.vpn_bot.config;

import com.example.vpn_bot.entity.payment.CryptoWallet;
import com.example.vpn_bot.repository.CryptoWalletRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "crypto")
public class CryptoWalletConfig {

    private final CryptoWalletRepo cryptoWalletRepo;
    private List<CryptoWallet> wallets; // Удалите @Value

    @Autowired
    public CryptoWalletConfig(CryptoWalletRepo cryptoWalletRepo) {
        this.cryptoWalletRepo = cryptoWalletRepo;
    }

    // Добавьте геттер и сеттер
    public List<CryptoWallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<CryptoWallet> wallets) {
        this.wallets = wallets;
    }

    @PostConstruct
    public void initWallets() {

        // Добавленная проверка на null
        if (cryptoWalletRepo == null) {
            throw new IllegalStateException("CryptoWalletRepo dependency not injected!");
        }

        if (wallets == null || wallets.isEmpty()) {
            throw new IllegalStateException("No crypto wallets configured!");
        }

        cryptoWalletRepo.deleteAll();
        cryptoWalletRepo.saveAll(wallets);
    }
}
