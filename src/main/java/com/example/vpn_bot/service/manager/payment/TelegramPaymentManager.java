package com.example.vpn_bot.service.manager.payment;

import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.telegram.Bot;
import com.example.vpn_bot.telegram.TelegramProperties;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.CHANGE_PERIOD_1;
import static com.example.vpn_bot.service.data.CallbackData.CHANGE_PERIOD_3;
import static com.example.vpn_bot.service.data.CallbackData.CHANGE_PERIOD_6;
import static com.example.vpn_bot.service.data.CallbackData.CHANGE_PERIOD_12;

// Создает инвойсы для оплаты через Telegram
// Определяет стоимость и описание для каждого периода
// Удаляет предыдущее сообщение с кнопками
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramPaymentManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final TelegramProperties telegramProperties;

    @Autowired
    public TelegramPaymentManager(AnswerMethodFactory methodFactory,
                                  TelegramProperties telegramProperties) {
        this.methodFactory = methodFactory;
        this.telegramProperties = telegramProperties;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null; // Не используется для команд
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null; // Не используется для сообщений
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        int months = 0;
        int amount = 0; // сумма в копейках
        String description = "";

        // Определение периода и стоимости подписки
        switch (data) {
            case CHANGE_PERIOD_1 -> {
                months = 1;
                amount = 29000; // 290 рублей * 100
                description = "VPN на 1 месяц";
            }
            case CHANGE_PERIOD_3 -> {
                months = 3;
                amount = 79000;
                description = "VPN на 3 месяца";
            }
            case CHANGE_PERIOD_6 -> {
                months = 6;
                amount = 149000;
                description = "VPN на 6 месяцев";
            }
            case CHANGE_PERIOD_12 -> {
                months = 12;
                amount = 249000;
                description = "VPN на 1 год";
            }
            default -> {
                return null; // Неподдерживаемый период
            }
        }

        try {
            // Удаление предыдущего сообщения с кнопками
            bot.execute(new DeleteMessage(chatId.toString(), callbackQuery.getMessage().getMessageId()));

            // Создание цены для платежа
            List<LabeledPrice> prices = new ArrayList<>();
            prices.add(new LabeledPrice(description, amount));

            // Создание инвойса
            SendInvoice sendInvoice = SendInvoice.builder()
                    .chatId(chatId.toString())
                    .title("Оплата подписки VPN")
                    .description(description)
                    .payload(chatId + "_" + months) // Передаем chatId и период
                    .providerToken(telegramProperties.getPaymentToken())
                    .currency("RUB") // Валюта - рубли
                    .prices(prices)
                    .photoUrl("https://example.com/vpn-logo.jpg") // URL логотипа
                    .needPhoneNumber(false)
                    .needShippingAddress(false)
                    .isFlexible(false)
                    .build();

            // Отправка инвойса
            bot.execute(sendInvoice);
            return null; // Для callbackQuery не возвращаем сообщение
        } catch (TelegramApiException e) {
            // Обработка ошибки при создании платежа
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Ошибка при создании платежа: " + e.getMessage(),
                    null
            );
        }

    }
}
