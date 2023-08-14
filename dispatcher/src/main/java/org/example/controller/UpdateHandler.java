package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.RabbitQueue;
import org.example.service.UpdateProducer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateHandler {
    private final TelegramBot telegramBot;
    private final UpdateProducer updateProducer;

    public void handleUpdate(Update update){
        if(update == null){
            return;
        }

        if (update.hasMessage()){
            handleUpdateByType(update);
        }else {
            log.error("Unsupported message type!");
        }
    }

    public void handleUpdateByType(Update update){
        Message message = update.getMessage();
        if(message.hasText()){
            handleTextMessage(update);
        } else if (message.hasDocument()){
            handleDocMessage(update);
        } else if (message.hasPhoto()) {
            handlePhotoMessage(update);
        }else{
            telegramBot.sendMessage(update, "Unsupported message type!");
        }
    }

    public void sendFileIsReceived(Update update){
        telegramBot.sendMessage(update, "Файл обрабатывается...");
    }

    public void sendMessage(SendMessage sendMessage){
        telegramBot.sendMessage(sendMessage);
    }

    private void handlePhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        sendFileIsReceived(update);
    }
    private void handleDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        sendFileIsReceived(update);
    }
    public void handleTextMessage(Update update){
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
