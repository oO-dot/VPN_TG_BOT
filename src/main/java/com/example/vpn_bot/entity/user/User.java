package com.example.vpn_bot.entity.user;

import com.example.vpn_bot.entity.partner.PartnerService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @Column(name = "id")
    Long chatId;

    @Column(name = "token")
    UUID token;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    Action action;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_details_id")
    UserDetails details;

    // Новые поля для системы оплаты
    @Column(name = "selected_period")
    Integer selectedPeriod; // Выбранный период подписки (в месяцах: 1, 3, 6, 12)

    @Column(name = "payment_amount")
    Double paymentAmount; // Сумма к оплате

    @Column(name = "wallet_address")
    String walletAddress; // Крипто-кошелек для оплаты

    @Column(name = "subscription_start")
    LocalDateTime subscriptionStart; // Новое поле: дата начала подписки

    @Column(name = "subscription_end")
    LocalDateTime subscriptionEnd; // Дата окончания подписки

    @Column(name = "wireguard_config", columnDefinition = "TEXT")
    String wireguardConfig; // Хранение конфигурации

    @ManyToOne
    @JoinColumn(name = "partner_service_id")
    PartnerService partnerService;



    @PrePersist
    private void generateUniqueToken() {
        if (token == null) {
            token = UUID.randomUUID();
        }
    }

}
