package com.example.vpn_bot.entity.payment;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "crypto_wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id; // Уникальный ID кошелька

    @Column(nullable = false)
    String currency; // Валюта: USDT, BTC, ETH и т.д.

    @Column(nullable = false, unique = true)
    String walletAddress; // Адрес кошелька

    @Column(nullable = false)
    String qrCodePath; // Путь к QR-коду для оплаты

}
