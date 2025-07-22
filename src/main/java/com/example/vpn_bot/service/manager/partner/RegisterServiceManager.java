package com.example.vpn_bot.service.manager.partner;


import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.service.partner.PartnerServiceManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class RegisterServiceManager extends AbstractManager {

    private final PartnerServiceManager partnerServiceManager;
    private final AnswerMethodFactory methodFactory;

    @Value("${telegram-bot.username}") // Инжектим имя бота
    private String botUsername;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        String[] parts = message.getText().split(" ", 2);

        // Проверка формата команды
        if (parts.length < 2) {
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Неверный формат команды.\n" +
                            "Используйте: /register_service <название_сервиса>\n" +
                            "Пример: /register_service MyVPNService",
                    null
            );
        }

        String serviceName = parts[1].trim();
        String serviceCode = generateUniqueCode();

        try {
            partnerServiceManager.registerService(serviceName, chatId, serviceCode);

            // Генерация реферальной ссылки
            String referralLink = "https://t.me/" + botUsername + "?start=" + serviceCode;

            return methodFactory.getSendMessage(
                    chatId,
                    "✅ Сервис '" + serviceName + "' успешно зарегистрирован!\n\n" +
                            "Реферальная ссылка для привлечения клиентов:\n" +
                            referralLink + "\n\n" +
                            "Теперь все подтвержденные платежи в этом чате будут учитываться в статистике сервиса.",
                    null
            );
        } catch (Exception e) {
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Ошибка регистрации сервиса: " + e.getMessage(),
                    null
            );
        }
    }

    // Генерация уникального кода
    private String generateUniqueCode() {
        return "svc_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }

}
