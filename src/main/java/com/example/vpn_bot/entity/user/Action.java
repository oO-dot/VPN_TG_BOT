package com.example.vpn_bot.entity.user;

public enum Action {
    FREE,           // Бесплатный пользователь
    SUBSCRIBER,     // Подписчик (активная подписка)
    AWAITING_PAYMENT, // Ожидает подтверждения оплаты
    PAYMENT_CONFIRMED // Оплата подтверждена (ожидает активации)
}
