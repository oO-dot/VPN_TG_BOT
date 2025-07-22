package com.example.vpn_bot.service.handler;

import com.example.vpn_bot.service.admin.AdminManager;
import com.example.vpn_bot.service.manager.change_period.ChangePeriodManager;
import com.example.vpn_bot.service.manager.feedback.FeedbackManager;
import com.example.vpn_bot.service.manager.help.HelpManager;
import com.example.vpn_bot.service.manager.instruction.InstructionManager;
import com.example.vpn_bot.service.manager.payment.PaymentManager;
import com.example.vpn_bot.service.manager.profile.ProfileManager;
import com.example.vpn_bot.service.manager.start.StartManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.example.vpn_bot.service.data.CallbackData.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {
    final HelpManager helpManager;
    final FeedbackManager feedbackManager;
    final InstructionManager instructionManager;
    final ChangePeriodManager changePeriodManager;
    final StartManager startManager;
    final ProfileManager profileManager;
    final PaymentManager paymentManager;
    final AdminManager adminManager;

    @Autowired
    public CallbackQueryHandler(HelpManager helpManager,
                                FeedbackManager feedbackManager,
                                InstructionManager instructionManager,
                                ChangePeriodManager changePeriodManager,
                                StartManager startManager,
                                ProfileManager profileManager,
                                PaymentManager paymentManager,
                                AdminManager adminManager) {
        this.helpManager = helpManager;
        this.feedbackManager = feedbackManager;
        this.instructionManager = instructionManager;
        this.changePeriodManager = changePeriodManager;
        this.startManager = startManager;
        this.profileManager = profileManager;
        this.paymentManager = paymentManager;
        this.adminManager = adminManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        // String keyWord = callbackData.split("_")[0];

        // Обработка админских действий
        if (callbackData.startsWith("ADMIN_")) {
            return adminManager.answerCallbackQuery(callbackQuery, bot);
        }

        switch (callbackData) {
            case FEEDBACK -> {
                return feedbackManager.answerCallbackQuery(callbackQuery, bot);
            }
            case HELP -> {
                return helpManager.answerCallbackQuery(callbackQuery, bot);
            }
            case INSTRUCTION -> {
                return instructionManager.answerCallbackQuery(callbackQuery, bot);
            }
            case CHANGE_PERIOD -> {
                return changePeriodManager.answerCallbackQuery(callbackQuery, bot);
            }
            case BACK_START -> {
                return startManager.answerCallbackQuery(callbackQuery, bot);
            }
            case PROFILE -> {
                return profileManager.answerCallbackQuery(callbackQuery, bot);
            }
            case PAYMENT_CONFIRMED -> {
                return paymentManager.answerCallbackQuery(callbackQuery, bot);
            }
            case CHANGE_PERIOD_1, CHANGE_PERIOD_3, CHANGE_PERIOD_6, CHANGE_PERIOD_12 -> {
                return changePeriodManager.answerCallbackQuery(callbackQuery, bot);
            }
            case GET_VPN_CONFIG -> {
                return profileManager.answerCallbackQuery(callbackQuery, bot);
            }
            default -> {
                return null;
            }
        }
    }
}
