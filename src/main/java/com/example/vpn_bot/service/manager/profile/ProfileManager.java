package com.example.vpn_bot.service.manager.profile;

import com.example.vpn_bot.repository.UserRepo;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.BACK_START;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final UserRepo userRepo;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public ProfileManager(AnswerMethodFactory methodFactory,
                          UserRepo userRepo,
                          KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.userRepo = userRepo;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return showProfile(message);
    }


    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        StringBuilder text = new StringBuilder("\uD83D\uDC64 Профиль\n");

        var user = userRepo.findById(chatId).orElseThrow();
        var details = user.getDetails();

        if (details.getUsername() == null) {
            text.append("\n▪\uFE0FИмя пользователя - ").append(details.getFirstName());
        } else {
            text.append("\n▪\uFE0FИмя пользователя - ").append(details.getUsername());
        }
        text.append("\n▪\uFE0FВаш id - ").append(details.getId());
        text.append("\n▪\uFE0FДата регистрации - ").append(details.getRegisteredAt());

        text.append("\n▪\uFE0FРоль - ").append(user.getRole().name());
        text.append("\n▪\uFE0FПодписка - ").append(user.getAction().name());
        text.append("\n▪\uFE0FВаш уникальный токен - \n").append(user.getToken().toString());

        // Добавлена информация о подписке
        text.append("\n\n\uD83D\uDCC5 Начало подписки: ");
        text.append(user.getSubscriptionStartDate() != null ?
                formatDate(user.getSubscriptionStartDate()) : "нет");

        text.append("\n\uD83D\uDDD3 Окончание подписки: ");
        text.append(user.getSubscriptionEndDate() != null ?
                formatDate(user.getSubscriptionEndDate()) : "нет");

        text.append("\n\n\uFE0F - токен нужен при обращении в тех поддержку, скопируйте и вставьте свой токен, далее опишите проблему при ее возникновении.");

        return methodFactory.getEditeMessageText(
                callbackQuery,
                text.toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_START)
                )
        );
    }

    // Форматирование даты для отображения
    private String formatDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }


    private BotApiMethod<?> showProfile(Message message) {

        Long chatId = message.getChatId();
        StringBuilder text = new StringBuilder("\uD83D\uDC64 Профиль\n");

        var user = userRepo.findById(chatId).orElseThrow();
        var details = user.getDetails();

        if (details.getUsername() == null) {
            text.append("\n▪\uFE0FИмя пользователя - ").append(details.getUsername());
        } else {
            text.append("\n▪\uFE0FИмя пользователя - ").append(details.getFirstName());
        }
        text.append("\n▪\uFE0FВаш id - ").append(details.getId());
        text.append("\n▪\uFE0FДата регистрации - ").append(details.getRegisteredAt());

        text.append("\n▪\uFE0FРоль - ").append(user.getRole().name());
        text.append("\n▪\uFE0FПодписка - ").append(user.getAction().name());
        text.append("\n▪\uFE0FВаш уникальный токен - ").append(user.getToken().toString());
        text.append("\n\n\uFE0F - токен нужен при обращении в тех поддержку, скопируйте и вставьте свой токен, далее опишите проблему при ее возникновении.");

        return methodFactory.getSendMessage(
                chatId,
                text.toString(),
                null
        );
    }
}