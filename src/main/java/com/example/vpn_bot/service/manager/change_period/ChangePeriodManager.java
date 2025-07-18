package com.example.vpn_bot.service.manager.change_period;

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

import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePeriodManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public ChangePeriodManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Выберите период на который хотите получить VPN
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("1 мес.", "3 мес.", "6 мес.", "1 год",
                                "Назад"),
                        List.of(4,
                                1),
                        List.of(CHANGE_PERIOD_1, CHANGE_PERIOD_3, CHANGE_PERIOD_6, CHANGE_PERIOD_12,
                                BACK_START)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditeMessageText(
                callbackQuery,
                """
                        Выберите период на который хотите получить VPN
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("1 мес.", "3 мес.", "6 мес.", "1 год",
                                "Назад"),
                        List.of(4,
                                1),
                        List.of(CHANGE_PERIOD_1, CHANGE_PERIOD_3, CHANGE_PERIOD_6, CHANGE_PERIOD_12,
                                BACK_START)
                )
        );
    }

}
