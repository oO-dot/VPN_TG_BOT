package com.example.vpn_bot.service.handler;

import com.example.vpn_bot.service.manager.change_period.ChangePeriodManager;
import com.example.vpn_bot.service.manager.feedback.FeedbackManager;
import com.example.vpn_bot.service.manager.help.HelpManager;
import com.example.vpn_bot.service.manager.instruction.InstructionManager;
import com.example.vpn_bot.service.manager.profile.ProfileManager;
import com.example.vpn_bot.service.manager.start.StartManager;
import com.example.vpn_bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


import static com.example.vpn_bot.service.data.Command.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandHandler {
    final FeedbackManager feedbackManager;
    final HelpManager helpManager;
    final StartManager startManager;
    final InstructionManager instructionManager;
    final ChangePeriodManager changePeriodManager;
    final ProfileManager profileManager;
    @Autowired
    public CommandHandler(FeedbackManager feedbackManager,
                          HelpManager helpManager,
                          StartManager startManager,
                          InstructionManager instructionManager,
                          ChangePeriodManager changePeriodManager,
                          ProfileManager profileManager) {
        this.feedbackManager = feedbackManager;
        this.helpManager = helpManager;
        this.startManager = startManager;
        this.instructionManager = instructionManager;
        this.changePeriodManager = changePeriodManager;
        this.profileManager = profileManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START -> {
                return startManager.answerCommand(message, bot);
            }
            case FEEDBACK_COMMAND -> {
                return feedbackManager.answerCommand(message, bot);
            }
            case HELP_COMMAND -> {
                return helpManager.answerCommand(message, bot);
            }
            case INSTRUCTION_COMMAND -> {
                return instructionManager.answerCommand(message, bot);
            }
            case CHANGE_PERIOD -> {
                return changePeriodManager.answerCommand(message, bot);
            }
            case PROFILE -> {
                return profileManager.answerCommand(message, bot);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .text("Неподдерживаемая команда!")
                .chatId(message.getChatId())
                .build();
    }
}
