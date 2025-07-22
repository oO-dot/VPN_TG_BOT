package com.example.vpn_bot.service.manager.change_period;

import com.example.vpn_bot.entity.partner.PartnerService;
import com.example.vpn_bot.entity.user.Action;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        return showYearlySubscription(message.getChatId());
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
                return showYearlySubscription(chatId);

            case CHANGE_PERIOD_12:
                return processYearlySubscription(chatId, callbackQuery);

            case BACK_START:
                return startManager.answerCallbackQuery(callbackQuery, bot);

            default:
                return null;
        }
    }

    private BotApiMethod<?> showYearlySubscription(Long chatId) {
        User user = userRepo.findById(chatId).orElse(null);
        if (user == null) {
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Пользователь не найден. Пожалуйста, начните с команды /start.",
                    null
            );
        }

        double price = 50.0; // Стандартная цена
        if (user.getPartnerService() != null) {
            PartnerService service = user.getPartnerService();
            price = service.getYearlyPrice();
        }

        String messageText;
        if (user.getAction() == Action.PAYMENT_CONFIRMED &&
                user.getSubscriptionEnd() != null &&
                user.getSubscriptionEnd().isAfter(LocalDateTime.now())) {

            // Если подписка активна - показываем информацию о продлении
            messageText = "⏳ *Продление подписки на 1 год*\n\n" +
                    "Текущая подписка активна до: " +
                    user.getSubscriptionEnd().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "\n" +
                    "Стоимость продления: " + price + " USD\n\n" +
                    "При оплате подписка будет продлена на 1 год с текущей даты окончания.";
        } else {
            // Новая подписка
            messageText = "⏳ *Подписка на 1 год*\n\n" +
                    "Стоимость: " + price + " USD";
        }
        return methodFactory.getSendMessage(
                chatId,
                messageText,
                keyboardFactory.getInlineKeyboard(
                        List.of("Оплатить " + price + " USD", "◀️ Назад"),
                        List.of(1, 1),
                        List.of(CHANGE_PERIOD_12, BACK_START)
                )
        );
    }

    private BotApiMethod<?> processYearlySubscription(Long chatId, CallbackQuery callbackQuery) {
        User user = userRepo.findById(chatId).orElse(null);
        if (user == null) {
            return methodFactory.getSendMessage(
                    chatId,
                    "❌ Пользователь не найден. Пожалуйста, начните с команды /start.",
                    null
            );
        }

        double price = 50.0;
        if (user.getPartnerService() != null) {
            price = user.getPartnerService().getYearlyPrice();
        }

        user.setSelectedPeriod(12);
        user.setPaymentAmount(price);
        userRepo.save(user);

        return paymentManager.initiatePayment(chatId, callbackQuery);
    }
}