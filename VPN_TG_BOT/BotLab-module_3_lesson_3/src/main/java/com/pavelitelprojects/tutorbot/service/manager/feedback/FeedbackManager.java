package com.pavelitelprojects.tutorbot.service.manager.feedback;

import com.pavelitelprojects.tutorbot.service.factory.AnswerMethodFactory;
import com.pavelitelprojects.tutorbot.service.factory.KeyboardFactory;
import com.pavelitelprojects.tutorbot.service.manager.AbstractManager;
import com.pavelitelprojects.tutorbot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.pavelitelprojects.tutorbot.service.data.CallbackData.BACK_START;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public FeedbackManager(AnswerMethodFactory methodFactory,
                           KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        📍 Ссылки для обратной связи
                        Почта - vpn_booster@mail.ru
                        Чат с тех. поддержкой - ссылка на чат
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_START)
                )
        );
    }
    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditeMessageText(
                callbackQuery,
                """
                        📍 Ссылки для обратной связи
                        Почта - vpn_booster@mail.ru
                        Чат с тех. поддержкой - ссылка на чат
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_START)
                )
        );
    }
    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

}
