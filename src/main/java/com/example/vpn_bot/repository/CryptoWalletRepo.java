package com.example.vpn_bot.repository;

import com.example.vpn_bot.entity.payment.CryptoWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CryptoWalletRepo extends JpaRepository<CryptoWallet, UUID> {

    // Получение случайного кошелька для оплаты
    @Query(value = "SELECT * FROM crypto_wallets ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    CryptoWallet findRandomWallet();

}
