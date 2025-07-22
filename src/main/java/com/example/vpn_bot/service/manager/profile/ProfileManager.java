package com.example.vpn_bot.service.manager.profile;

import com.example.vpn_bot.entity.user.Action;
import com.example.vpn_bot.entity.user.User;
import com.example.vpn_bot.repository.UserRepo;
import com.example.vpn_bot.service.WireGuardConfigService;
import com.example.vpn_bot.service.data.CallbackData;
import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.factory.KeyboardFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.BACK_START;
import static com.example.vpn_bot.service.data.CallbackData.GET_VPN_CONFIG;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final UserRepo userRepo;
    final KeyboardFactory keyboardFactory;

    @Value("${vpn.config.stub-url}")  // Инжектим значение из application.yml
    private String vpnConfigStubUrl;

    @Autowired
    public ProfileManager(AnswerMethodFactory methodFactory,
                          UserRepo userRepo,
                          KeyboardFactory keyboardFactory
    ) {
        this.methodFactory = methodFactory;
        this.userRepo = userRepo;
        this.keyboardFactory = keyboardFactory;
    }

    // Общий метод для создания клавиатуры профиля (Создает клавиатуру для профиля в зависимости от статуса пользователя)
    private InlineKeyboardMarkup createProfileKeyboard(User user) {
        List<String> buttonTexts = new ArrayList<>();
        List<String> callbackDatas = new ArrayList<>();

        // Добавляем VPN-кнопку только для подтвержденных платежей
        if (user.getAction() == Action.PAYMENT_CONFIRMED) {
            buttonTexts.add("Скачать VPN конфиг");
            callbackDatas.add(GET_VPN_CONFIG);
        }

        // Всегда добавляем кнопку "Назад"
        buttonTexts.add("Назад");
        callbackDatas.add(BACK_START);


        // Создаем конфигурацию рядов (по 1 кнопке в каждом ряду)
        List<Integer> rowLayout = new ArrayList<>();
        for (int i = 0; i < buttonTexts.size(); i++) {
            rowLayout.add(1);
        }

        return keyboardFactory.getInlineKeyboard(
                buttonTexts,
                rowLayout,
                callbackDatas
        );
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {

        Long chatId = message.getChatId();
        StringBuilder text = new StringBuilder("\uD83D\uDC64 Профиль\n");

        User user = userRepo.findById(chatId).orElseThrow();
        var details = user.getDetails();

        // Формирование текста профиля
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
        text.append("\n\n\uFE0F - токен нужен при обращении в тех поддержку, скопируйте и вставьте свой токен, далее опишите проблему при ее возникновении.");

        return methodFactory.getSendMessage(
                chatId,
                text.toString(),
                createProfileKeyboard(user)  // Используем общий метод для создания клавиатуры
        );

    }


    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {

        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        // Обработка запроса на получение VPN-конфига
        if (GET_VPN_CONFIG.equals(callbackData)) {
            return sendVpnConfig(chatId);
        }

        // Формирование текста профиля (аналогично методу answerCommand)
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

        text.append("\n\n\uFE0F - токен нужен при обращении в тех поддержку, скопируйте и вставьте свой токен, далее опишите проблему при ее возникновении.");

        return methodFactory.getEditeMessageText(
                callbackQuery,
                text.toString(),
                createProfileKeyboard(user)  // Используем общий метод для создания клавиатуры
        );

    }

    // Отправляет сообщение со ссылкой на VPN конфиг
    private BotApiMethod<?> sendVpnConfig(Long chatId) {
        return methodFactory.getSendMessage(
                chatId,
                "✅ Ваша конфигурация готова!\n" +
                        "Скачайте файл: " + vpnConfigStubUrl + "\n\n" +
                        "Инструкция по установке:\n" +
                        "1. Скачайте приложение WireGuard\n" +
                        "2. Импортируйте этот конфиг\n" +
                        "3. Активируйте подключение",
                null
        );
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