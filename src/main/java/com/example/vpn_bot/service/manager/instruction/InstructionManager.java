package com.example.vpn_bot.service.manager.instruction;

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

import static com.example.vpn_bot.service.data.CallbackData.BACK_START;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructionManager extends AbstractManager {

    //TODO
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public InstructionManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Здесь будет инструкция по вставке QR-code через WireGuard
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

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditeMessageText(
                callbackQuery,
                """
                        Здесь будет инструкция по вставке QR-code через WireGuard
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_START)
                )
        );
    }
}
