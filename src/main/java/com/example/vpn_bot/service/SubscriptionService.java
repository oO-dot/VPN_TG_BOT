package com.example.vpn_bot.service;

import com.example.vpn_bot.entity.user.Action;
import com.example.vpn_bot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// Обновляет подписку пользователя
// Продлевает существующую подписку или создает новую
// Устанавливает статус пользователя как подписчик
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionService {

    final UserRepo userRepo;

    @Autowired
    public SubscriptionService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void updateSubscription(Long userId, int months) {
        userRepo.findById(userId).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now();

            // Если подписка активна, продлеваем ее
            if (user.getSubscriptionEndDate() != null &&
                    user.getSubscriptionEndDate().isAfter(now)) {
                user.setSubscriptionEndDate(
                        user.getSubscriptionEndDate().plusMonths(months)
                );
            } else {
                // Новая подписка
                user.setSubscriptionStartDate(now);
                user.setSubscriptionEndDate(now.plusMonths(months));
            }
            user.setAction(Action.SUBSCRIBER);
            userRepo.save(user);
        });
    }

}
