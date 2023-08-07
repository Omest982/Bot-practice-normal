package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.RawData;
import org.example.repository.RawDataRepository;
import org.example.service.AnswerProducer;
import org.example.service.MainService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository rawDataRepository;
    private final AnswerProducer answerProducer;
    @Override
    public void processTextMessage(Update update) {
        rawDataRepository.save(null);
        answerProducer.produceAnswer(new SendMessage(String.valueOf(
                update.getMessage().getChatId())
                , "Saved success"));
    }
}
