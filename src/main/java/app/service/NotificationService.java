package app.service;

import app.integration.telegram.RCNotificator;
import app.model.Bond;
import app.model.Env;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static app.config.TelegramConfig.TESTERS;
import static app.config.TelegramConfig.CLIENTS;

@Slf4j
public class NotificationService
{
    RCNotificator rcNotificator;
    String[] users;

    public NotificationService(Env env)
    {
        switch (env)
        {

            case TEST -> users = TESTERS ;
            case PROD -> users = CLIENTS ;
            default -> throw new NoSuchElementException("INVALID_ENV creating telegram bot env=%s"
                    .formatted(env)
            );
        }
        rcNotificator = new RCNotificator();
    }

    public void broadcastMessage(String message)
    {
        for (String userId : users)
        {
            sendMessage(userId, message);
        }
    }

    public void sendMessage(String chatId, String message)
    {
        log.info("Send message={} to user={}",
                message,
                chatId
        );
        rcNotificator.sendMessage(chatId, message);
    }
 }
