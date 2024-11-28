package app.integration.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static app.config.TelegramConfig.ACCESS_TOKEN;
import static app.config.TelegramConfig.BOT_USERNAME;

public class RCNotificator extends TelegramLongPollingBot
{
    @Override
    public void onUpdateReceived(Update update) {}

    @Override
    public String getBotUsername()
    {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken()
    {
        return ACCESS_TOKEN;
    }

    public void sendMessage(String chatId, String text)
    {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try
        {
            execute(message);
        }
        catch (TelegramApiException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
