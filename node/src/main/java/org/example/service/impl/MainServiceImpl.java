package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.AppUser;
import org.example.entity.RawData;
import org.example.repository.AppUserRepository;
import org.example.repository.RawDataRepository;
import org.example.service.AnswerProducer;
import org.example.service.MainService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.example.entity.enums.UserStatus.BASIC_STATUS;

@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository rawDataRepository;
    private final AppUserRepository appUserRepository;
    private final AnswerProducer answerProducer;
    @Override
    public void processTextMessage(Update update) {
        AppUser user = findOrSaveAppUser(update);
        saveRawData(update);
        answerProducer.produceAnswer(new SendMessage(String.valueOf(
                update.getMessage().getChatId())
                , "Saved success"));
    }

    public void saveRawData(Update update){
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataRepository.save(rawData);
    }

    public AppUser findOrSaveAppUser(Update update){
        User user = update.getMessage().getFrom();
        AppUser persistentUser = appUserRepository.findAppUserByTelegramUserId(user.getId());
        if (persistentUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUserName())
                    //TODO изменить значения по умолчанию после добовления регистрации
                    .isActive(true)
                    .status(BASIC_STATUS)
                    .build();
            appUserRepository.save(transientAppUser);
            return transientAppUser;
        }
        return persistentUser;
    }
}
