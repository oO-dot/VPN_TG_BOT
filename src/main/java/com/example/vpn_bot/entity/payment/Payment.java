package com.example.vpn_bot.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// класс для оплаты в ТГ боте
@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    String telegramPaymentId;  // ID платежа в Telegram

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    String currency;

    @Column(nullable = false)
    String status;

    @Column(nullable = false)
    LocalDateTime createdDate;

    @Column
    LocalDateTime paymentDate;

    @Column(nullable = false)
    Long userId;  // chatId пользователя

    @Column(nullable = false)
    int periodMonths;  // Период подписки в месяцах
}
