package com.pavelitelprojects.tutorbot.service.manager.start;

import com.pavelitelprojects.tutorbot.service.factory.AnswerMethodFactory;
import com.pavelitelprojects.tutorbot.service.factory.KeyboardFactory;
import com.pavelitelprojects.tutorbot.service.manager.AbstractManager;
import com.pavelitelprojects.tutorbot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.pavelitelprojects.tutorbot.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public StartManager(AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }
    @Override
    public SendMessage answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        🖖 Вас приветствует VPN_BOOSTER, лучший VPN для работы за границей или просмотра Instagram, YouTube, а также других развлекательных целей и не только!
                        
                        👨‍💻 Лучшая тех поддержка, работаем 24/7
                        🚀 Скорость гарантирована
                        
                        📲 Как подключиться❔
                        1️⃣ Выбери период
                        2️⃣ Получи QR-code
                        3️⃣ Отсканируй QR-code или вставь его в приложение WireGuard.
                        
                        📝 Подробная инструкция по вставке QR-code через приложение WireGuard КЛИК ⬇️ .
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Выбрать период пользования VPN", "Инструкция по вставке QR-code через WireGuard", "Профиль", "Помощь", "Обратная связь"),
                        List.of(1, 1, 1, 2),
                        List.of(CHANGE_PERIOD, INSTRUCTION, PROFILE, HELP, FEEDBACK ) // сделать CHANGE_PERIOD, PROFILE
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
                        🖖 Вас приветствует VPN_BOOSTER, лучший VPN для работы за границей или просмотра Instagram, YouTube, а также других развлекательных целей и не только!
                        
                        👨‍💻 Лучшая тех поддержка, работаем 24/7
                        🚀 Скорость гарантирована
                        
                        📲 Как подключиться❔
                        1️⃣ Выбери период
                        2️⃣ Получи QR-code
                        3️⃣ Отсканируй QR-code или вставь его в приложение WireGuard.
                        
                        📝 Подробная инструкция по вставке QR-code через приложение WireGuard КЛИК ⬇️ .
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Выбрать период пользования VPN", "Инструкция по вставке QR-code через WireGuard", "Профиль", "Помощь", "Обратная связь"),
                        List.of(1, 1, 1, 2),
                        List.of(CHANGE_PERIOD, INSTRUCTION, PROFILE, HELP, FEEDBACK ) // сделать CHANGE_PERIOD, PROFILE
                )
        );
    }

}
