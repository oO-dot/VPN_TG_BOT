package com.example.vpn_bot.service.admin;

import com.example.vpn_bot.entity.user.Action;
import com.example.vpn_bot.entity.user.User;
import com.example.vpn_bot.repository.UserRepo;
import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.factory.KeyboardFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.service.partner.PartnerServiceManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    // Репозиторий для работы с пользователями
    final UserRepo userRepo;
    // Фабрика для создания клавиатур
    final KeyboardFactory keyboardFactory;
    final PartnerServiceManager partnerServiceManager;
    // Экземпляр бота для отправки сообщений
    Bot bot;

    // ID админ-чата из конфигурации

    @Autowired
    public AdminManager(UserRepo userRepo,
                        AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory,
                        PartnerServiceManager partnerServiceManager,
                        @Lazy Bot bot) {
        // Вызываем конструктор родительского класса
        this.userRepo = userRepo;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.partnerServiceManager = partnerServiceManager;
        this.bot = bot;
    }

    /**
     * Уведомляет администраторов о новом платеже
     * @param user Пользователь, совершивший платеж
     * @param walletAddress Адрес кошелька для оплаты
     */
    public void notifyNewPayment(User user, String walletAddress, String currency) {
        // Проверяем, что пользователь привязан к сервису
        if (user.getPartnerService() == null) {
            System.err.println("⚠️ User has no partner service: " + user.getChatId());
            return;
        }

        Long targetChatId = user.getPartnerService().getAdminChatId();

        // if (targetChatId == null) return; // Если сервис не привязан - не отправляем

        String text = "🔔 *Новый платеж!*\n\n" +
                "👤 Пользователь: " + user.getDetails().getFirstName() + "\n" +
                "💳 Сумма: " + user.getPaymentAmount() + " USDT\n" +
                "💱 Валюта: " + currency + "\n" +
                "🔗 Кошелек: `" + walletAddress + "`\n" +
                "🆔 ID: `" + user.getChatId() + "`";

        SendMessage message = methodFactory.getSendMessage(
                targetChatId, // Используем целевой чат
                text,
                keyboardFactory.getInlineKeyboard(
                        List.of("✅ Подтвердить оплату", "❌ Отклонить"),
                        List.of(2),
                        List.of(
                                "ADMIN_CONFIRM_" + user.getChatId(),
                                "ADMIN_REJECT_" + user.getChatId()
                        )
                )
        );

        try {
            bot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();

        // Обработка подтверждения платежа
        if (callbackData.startsWith("ADMIN_CONFIRM_")) {
            // Извлекаем ID пользователя из callback-данных
            Long userId = Long.parseLong(callbackData.split("_")[2]);
            return confirmPayment(userId, callbackQuery);
        }

        // Обработка отклонения платежа
        if (callbackData.startsWith("ADMIN_REJECT_")) {
            Long userId = Long.parseLong(callbackData.split("_")[2]);
            return rejectPayment(userId, callbackQuery);
        }

        return null;
    }

    /**
     * Подтверждает платеж и активирует подписку
     * @param userId ID пользователя
     * @param callbackQuery Callback-запрос
     */

    private BotApiMethod<?> confirmPayment(Long userId, CallbackQuery callbackQuery) {
        // Находим пользователя в базе
        User user = userRepo.findById(userId).orElseThrow(() -> {
            System.err.println("❌ User not found with chatId: " + userId);
            return new RuntimeException("User not found");
        });

        // Обновляем статус пользователя
        user.setAction(Action.PAYMENT_CONFIRMED);
        // Устанавливаем дату окончания подписки
        user.setSubscriptionEnd(LocalDateTime.now().plusMonths(user.getSelectedPeriod()));
        userRepo.save(user);

        System.out.println("🔵 Found user: " + user.getChatId() +
                " | Current status: " + user.getAction());

        // Увеличиваем счетчик клиентов сервиса
        if (user.getPartnerService() != null) {
            partnerServiceManager.incrementClientCount(
                    user.getPartnerService().getAdminChatId()
            );
        }

        // Формируем сообщение об успешном подтверждении
        String confirmationText = "✅ *Платеж подтвержден!*\n\n" +
                "👤 Пользователь: " + user.getDetails().getFirstName() + "\n" +
                "🆔 ID: `" + user.getChatId() + "`\n" +
                "📅 Подписка активна до: " +
                user.getSubscriptionEnd().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        // Возвращаем обновленное сообщение (убираем кнопки)
        return methodFactory.getEditeMessageText(
                callbackQuery,
                confirmationText,
                null
        );
    }

    /**
     * Отклоняет платеж
     * @param userId ID пользователя
     * @param callbackQuery Callback-запрос
     */
    private BotApiMethod<?> rejectPayment(Long userId, CallbackQuery callbackQuery) {
        User user = userRepo.findById(userId).orElseThrow(() -> {
            System.err.println("❌ User not found with chatId: " + userId);
            return new RuntimeException("User not found");
        });

        System.out.println("🔵 Found user: " + user.getChatId() +
                " | Current status: " + user.getAction());

        // Возвращаем пользователя в свободный статус
        user.setAction(Action.FREE);
        userRepo.save(user);

        // Формируем сообщение об отклонении
        String rejectionText = "❌ *Платеж отклонен!*\n\n" +
                "👤 Пользователь: " + user.getDetails().getFirstName() + "\n" +
                "🆔 ID: `" + user.getChatId() + "`";

        return methodFactory.getEditeMessageText(
                callbackQuery,
                rejectionText,
                null
        );
    }

    // Неиспользуемые методы интерфейса (для команд и сообщений)
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }
}