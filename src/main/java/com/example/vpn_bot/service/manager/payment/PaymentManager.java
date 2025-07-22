package com.example.vpn_bot.service.manager.payment;

import com.example.vpn_bot.entity.payment.CryptoWallet;
import com.example.vpn_bot.entity.user.Action;
import com.example.vpn_bot.entity.user.User;
import com.example.vpn_bot.repository.CryptoWalletRepo;
import com.example.vpn_bot.repository.UserRepo;
import com.example.vpn_bot.service.admin.AdminManager;
import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.factory.KeyboardFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.vpn_bot.service.data.CallbackData.*;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentManager extends AbstractManager {

    final UserRepo userRepo;
    final CryptoWalletRepo cryptoWalletRepo;
    final KeyboardFactory keyboardFactory;
    final AdminManager adminManager;
    final AnswerMethodFactory methodFactory; // Прямая инъекция

    @Autowired
    public PaymentManager(UserRepo userRepo,
                          CryptoWalletRepo cryptoWalletRepo,
                          KeyboardFactory keyboardFactory,
                          AdminManager adminManager,
                          AnswerMethodFactory methodFactory) {
        this.userRepo = userRepo;
        this.cryptoWalletRepo = cryptoWalletRepo;
        this.keyboardFactory = keyboardFactory;
        this.adminManager = adminManager;
        this.methodFactory = methodFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        if (PAYMENT_CONFIRMED.equals(callbackQuery.getData())) {
            return processPaymentConfirmation(callbackQuery.getMessage().getChatId(), callbackQuery);
        }
        return null;
    }

    public BotApiMethod<?> initiatePayment(Long chatId, CallbackQuery callbackQuery) {
        User user = userRepo.findById(chatId).orElseThrow();
        CryptoWallet wallet = cryptoWalletRepo.findRandomWallet();

        user.setAction(Action.AWAITING_PAYMENT);
        user.setWalletAddress(wallet.getWalletAddress());
        userRepo.save(user);

        adminManager.notifyNewPayment(user, wallet.getWalletAddress());

        return methodFactory.getEditeMessageText(
                callbackQuery,
                "💳 Оплатите " + user.getPaymentAmount() + " USDT\n\n" +
                        "На кошелек: " + wallet.getWalletAddress() + "\n" +
                        "Сеть: " + wallet.getCurrency() + "\n\n" +
                        "После оплаты нажмите кнопку ниже ⬇️",
                keyboardFactory.getInlineKeyboard(
                        List.of("✅ Я оплатил"),
                        List.of(1),
                        List.of(PAYMENT_CONFIRMED)
                )
        );
    }

    private BotApiMethod<?> processPaymentConfirmation(Long chatId, CallbackQuery callbackQuery) {
        return methodFactory.getEditeMessageText(
                callbackQuery,
                "✅ Ваша оплата принята в обработку!\n" +
                        "Администратор проверит платеж и активирует ваш VPN в течение 15 минут.\n" +
                        "Статус можно посмотреть в профиле (/profile)",
                null
        );
    }
}