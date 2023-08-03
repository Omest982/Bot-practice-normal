package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.service.AnswerProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
@RequiredArgsConstructor
public class AnswerProducerImpl implements AnswerProducer {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_UPDATE, sendMessage);
    }
}
