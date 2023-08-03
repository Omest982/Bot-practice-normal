package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.UpdateHandler;
import org.example.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateHandler updateHandler;
    @Override
    @RabbitListener(queues = ANSWER_MESSAGE_UPDATE)
    public void consume(SendMessage sendMessage) {
        updateHandler.sendMessage(sendMessage);
    }
}
