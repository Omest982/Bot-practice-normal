package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.UpdateConsumer;
import org.example.service.AnswerProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateConsumerImpl implements UpdateConsumer {

    private final AnswerProducer answerProducer;
    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(Update update) {
        log.info("NODE : TEXT MESSAGE RECEIVED");
        answerProducer.produceAnswer(new SendMessage(
                String.valueOf(update.getMessage().getChatId()),
                "NODE : TEXT MESSAGE RECEIVED"));
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessage(Update update) {
        log.info("NODE : DOC MESSAGE RECEIVED");
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessage(Update update) {
        log.info("NODE : PHOTO MESSAGE RECEIVED");
    }
}