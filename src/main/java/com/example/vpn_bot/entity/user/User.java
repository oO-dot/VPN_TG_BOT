package com.example.vpn_bot.entity.user;

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

    // Добавлено для управления подписками
    @Column(name = "subscription_start_date")
    LocalDateTime subscriptionStartDate;

    @Column(name = "subscription_end_date")
    LocalDateTime subscriptionEndDate;

    @PrePersist
    private void generateUniqueToken() {
        if (token == null) {
            token = UUID.randomUUID();
        }
    }

}
