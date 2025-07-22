package com.example.vpn_bot.entity.partner;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "partner_services")
@Getter
@Setter
public class PartnerService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceName; // Уникальное имя сервиса

    @Column(nullable = false, unique = true)
    private Long adminChatId; // ID чата администратора

    @Column(nullable = false)
    private Integer clientsCount = 0; // Счетчик привлеченных клиентов

    @Column(nullable = false, unique = true)
    private String serviceCode;  // Уникальный код для реферальных ссылок


    @Column(nullable = false, name = "tonkeeper_wallet")
    private String tonkeeperWallet; // Новое поле для кошелька Tonkeeper

    @Column(nullable = false)
    private Double yearlyPrice; // Новая колонка для стоимости годовой подписки

}
