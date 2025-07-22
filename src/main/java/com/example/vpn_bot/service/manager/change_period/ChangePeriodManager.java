package com.example.vpn_bot.service.manager.change_period;

import com.example.vpn_bot.entity.user.User;
import com.example.vpn_bot.repository.UserRepo;
import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.factory.KeyboardFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.service.manager.payment.PaymentManager;
import com.example.vpn_bot.service.manager.start.StartManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePeriodManager extends AbstractManager {

    final KeyboardFactory keyboardFactory;
    final PaymentManager paymentManager;
    final UserRepo userRepo;
    final StartManager startManager;
    final AnswerMethodFactory methodFactory;

    @Autowired
    public ChangePeriodManager(KeyboardFactory keyboardFactory,
                               PaymentManager paymentManager,
                               UserRepo userRepo,
                               StartManager startManager,
                               AnswerMethodFactory methodFactory) {
        this.keyboardFactory = keyboardFactory;
        this.paymentManager = paymentManager;
        this.userRepo = userRepo;
        this.startManager = startManager;
        this.methodFactory = methodFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return showPeriodSelection(message.getChatId());
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (callbackData) {
            case CHANGE_PERIOD:
                return showPeriodSelection(chatId);

            case CHANGE_PERIOD_1:
                return processPeriodSelection(chatId, 1, callbackQuery);

            case CHANGE_PERIOD_3:
                return processPeriodSelection(chatId, 3, callbackQuery);

            case CHANGE_PERIOD_6:
                return processPeriodSelection(chatId, 6, callbackQuery);

            case CHANGE_PERIOD_12:
                return processPeriodSelection(chatId, 12, callbackQuery);

            case BACK_START:
                return startManager.answerCallbackQuery(callbackQuery, bot);

            default:
                return null;
        }
    }

    // Показ меню выбора периода
    private BotApiMethod<?> showPeriodSelection(Long chatId) {
        return methodFactory.getSendMessage(
                chatId,
                "⏳ *Выберите период подписки:*",
                keyboardFactory.getInlineKeyboard(
                        List.of("1 мес. - $10", "3 мес. - $25", "6 мес. - $45", "1 год - $80", "◀️ Назад"),
                        List.of(4, 1),
                        List.of(
                                CHANGE_PERIOD_1,
                                CHANGE_PERIOD_3,
                                CHANGE_PERIOD_6,
                                CHANGE_PERIOD_12,
                                BACK_START
                        )
                )
        );
    }

    // Обработка выбора периода
    private BotApiMethod<?> processPeriodSelection(Long chatId, int months, CallbackQuery callbackQuery) {
        User user = userRepo.findById(chatId).orElse(null); // Возвращаем null вместо исключения

        if (user == null) {
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Пользователь не найден. Пожалуйста, начните с команды /start.",
                    null
            );
        }

        // Устанавливаем цену в зависимости от периода
        double price = switch (months) {
            case 1 -> 10.0;
            case 3 -> 25.0;
            case 6 -> 45.0;
            case 12 -> 80.0;
            default -> 0.0;
        };

        // Сохраняем выбранный период и цену
        user.setSelectedPeriod(months);
        user.setPaymentAmount(price);
        userRepo.save(user);

        // Перенаправляем в процесс оплаты
        return paymentManager.initiatePayment(chatId, callbackQuery);
    }
}