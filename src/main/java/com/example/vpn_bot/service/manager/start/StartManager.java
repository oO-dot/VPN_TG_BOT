package com.example.vpn_bot.service.manager.start;

import com.example.vpn_bot.entity.partner.PartnerService;
import com.example.vpn_bot.entity.user.Action;
import com.example.vpn_bot.entity.user.Role;
import com.example.vpn_bot.entity.user.User;
import com.example.vpn_bot.entity.user.UserDetails;
import com.example.vpn_bot.repository.PartnerServiceRepo;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.vpn_bot.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final PartnerServiceRepo partnerServiceRepo;
    final UserRepo userRepo;

    @Autowired
    public StartManager(AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory,
                        PartnerServiceRepo partnerServiceRepo,
                        UserRepo userRepo) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.partnerServiceRepo = partnerServiceRepo;
        this.userRepo = userRepo;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        User user = userRepo.findById(chatId).orElseGet(() -> createNewUser(message));

        // Обработка реферального кода
        String[] commandParts = message.getText().split("\\s+");
        if (commandParts.length > 1) {
            String serviceCode = commandParts[1];
            PartnerService service = partnerServiceRepo.findByServiceCode(serviceCode);

            if (service != null) {
                user.setPartnerService(service);
                userRepo.save(user);
            }

        }

        return createStartMessage(chatId);
    }

    private User createNewUser(Message message) {
        User user = new User();
        user.setChatId(message.getChatId());

        UserDetails details = new UserDetails();
        details.setFirstName(message.getFrom().getFirstName());
        details.setLastName(message.getFrom().getLastName());
        details.setUsername(message.getFrom().getUserName());
        details.setRegisteredAt(LocalDateTime.now());

        user.setDetails(details);
        details.setUser(user);

        user.setRole(Role.USER);
        user.setAction(Action.FREE);

        return userRepo.save(user);
    }



    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return createEditMessage(callbackQuery);
    }


    private BotApiMethod<?> createStartMessage(Long chatId) {

        return methodFactory.getSendMessage(
                chatId,
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
                        List.of("Оформить подписку на 1 год", "Инструкция по вставке QR-code через WireGuard", "Профиль", "Помощь", "Обратная связь"),
                        List.of(1, 1, 1, 2),
                        List.of(CHANGE_PERIOD, INSTRUCTION, PROFILE, HELP, FEEDBACK ) // сделать CHANGE_PERIOD, PROFILE
                )
        );

    }

    private BotApiMethod<?> createEditMessage(CallbackQuery callbackQuery) {

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
                        List.of("Оформить подписку на 1 год", "Инструкция по вставке QR-code через WireGuard", "Профиль", "Помощь", "Обратная связь"),
                        List.of(1, 1, 1, 2),
                        List.of(CHANGE_PERIOD, INSTRUCTION, PROFILE, HELP, FEEDBACK ) // сделать CHANGE_PERIOD, PROFILE
                )
        );

    }

}
