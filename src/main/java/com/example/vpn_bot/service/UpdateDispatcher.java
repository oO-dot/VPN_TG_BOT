package com.example.vpn_bot.service;

import com.example.vpn_bot.repository.UserRepo;
import com.example.vpn_bot.service.handler.CallbackQueryHandler;
import com.example.vpn_bot.service.handler.CommandHandler;
import com.example.vpn_bot.service.handler.MessageHandler;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UpdateDispatcher {
    final MessageHandler messageHandler;
    final CommandHandler commandHandler;
    final CallbackQueryHandler callbackQueryHandler;
    final UserRepo userRepo; // Добавлено для работы с подписками
    final SubscriptionService subscriptionService; // Добавлено для управления подписками

    @Autowired
    public UpdateDispatcher(MessageHandler messageHandler,
                            CommandHandler commandHandler,
                            CallbackQueryHandler callbackQueryHandler,
                            UserRepo userRepo,
                            SubscriptionService subscriptionService) {
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.userRepo = userRepo;
        this.subscriptionService = subscriptionService;
    }

    public BotApiMethod<?> distribute(Update update, Bot bot) {

        // Обработка предварительного запроса на оплату
        if (update.hasPreCheckoutQuery()) {
            return handlePreCheckout(update.getPreCheckoutQuery(), bot);
        }

        // Обработка успешного платежа
        if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            return handleSuccessfulPayment(update.getMessage(), bot);
        }

        // Обработка callback-запросов
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.answer(update.getCallbackQuery(), bot);
        }

        // Обработка текстовых сообщений и команд
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                if (message.getText().charAt(0) == '/') {
                    return commandHandler.answer(message, bot);
                }
            }
            return messageHandler.answer(message, bot);
        }

        // Логирование неподдерживаемых типов обновлений
        log.info("Unsupported update:" + update);
        return null;
    }

    // Обработка предварительного запроса на оплату
    private BotApiMethod<?> handlePreCheckout(PreCheckoutQuery preCheckoutQuery, Bot bot) {
        try {
            // Всегда подтверждаем предварительный запрос
            AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(preCheckoutQuery.getId())
                    .ok(true)
                    .build();

            bot.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error answering pre-checkout query", e);
        }
        return null;
    }

    // Обработка успешного платежа
    private BotApiMethod<?> handleSuccessfulPayment(Message message, Bot bot) {
        SuccessfulPayment payment = message.getSuccessfulPayment();
        String payload = payment.getInvoicePayload();
        String[] parts = payload.split("_");
        Long userId = Long.parseLong(parts[0]);
        int periodMonths = Integer.parseInt(parts[1]);

        // Обновляем подписку пользователя
        subscriptionService.updateSubscription(userId, periodMonths);

        // Отправляем сообщение об успешной оплате
        return SendMessage.builder()
                .text("✅ Платеж успешно завершен! Подписка активирована.")
                .chatId(message.getChatId())
                .build();
    }
}
