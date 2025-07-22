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
                        Инструкция по подключению VPN:
                        1. Нажмите "Скачать VPN конфиг" в боте
                        2. Скопируйте всю ссылку из сообщения
                        3. Откройте V2RayTun
                        4. Нажмите "+" в верхнем правом углу → "Добавить из буфера обмена"
                        5. Запустите подключение
                        
                        Скачать V2RayTun для:
                        iOS: https://apps.apple.com/ru/app/v2raytun/id6476628951
                        Android: https://play.google.com/store/apps/details?id=com.v2raytun
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
                        Инструкция по подключению VPN:
                        1. Нажмите "Скачать VPN конфиг" в боте
                        2. Скопируйте всю ссылку из сообщения
                        3. Откройте V2RayTun
                        4. Нажмите "+" в верхнем правом углу → "Добавить из буфера обмена"
                        5. Запустите подключение
                        
                        Скачать V2RayTun для:
                        iOS: https://apps.apple.com/ru/app/v2raytun/id6476628951
                        Android: https://play.google.com/store/apps/details?id=com.v2raytun
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_START)
                )
        );
    }
}
