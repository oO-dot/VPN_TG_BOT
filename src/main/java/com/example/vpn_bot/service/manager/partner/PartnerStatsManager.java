package com.example.vpn_bot.service.manager.partner;


import com.example.vpn_bot.service.factory.AnswerMethodFactory;
import com.example.vpn_bot.service.manager.AbstractManager;
import com.example.vpn_bot.service.partner.PartnerStatsService;
import com.example.vpn_bot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class PartnerStatsManager extends AbstractManager {

    private final PartnerStatsService statsService;
    private final AnswerMethodFactory methodFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        String stats = statsService.getServiceStats(chatId);
        return methodFactory.getSendMessage(chatId, stats, null);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }

}
