package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.AppUser;
import org.example.entity.RawData;
import org.example.entity.enums.UserStatus;
import org.example.repository.AppUserRepository;
import org.example.repository.RawDataRepository;
import org.example.service.AnswerProducer;
import org.example.service.AppUserService;
import org.example.service.FileService;
import org.example.service.MainService;
import org.example.service.enums.BotCommands;
import org.example.service.enums.LinkType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.example.entity.enums.UserStatus.*;
import static org.example.service.enums.BotCommands.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class MainServiceImpl implements MainService {
    private final RawDataRepository rawDataRepository;
    private final AppUserRepository appUserRepository;
    private final AnswerProducer answerProducer;
    private final AppUserService appUserService;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String result = "";

        BotCommands command = BotCommands.fromValue(text);
        if (CANCEL.equals(command)){
            result = cancelProcess(appUser);
        } else if (BASIC_STATUS.equals(appUser.getStatus())) {
            result = processServiceCommand(appUser, command);
        }else if(WAIT_FOR_EMAIL_STATUS.equals(appUser.getStatus())){
            result = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state " + appUser.getStatus());
            result = "Неизвестная ошибка! Введите /cancel и попоробуйте снова";
        }

        sendMessage(chatId, result);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if(isNotAllowedToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            String answer = "Документ успешно загружен!\n" +
                    "Ссылка для скачивания : " + link;
            sendMessage(chatId, answer);
        } catch (RuntimeException e){
            log.error(String.valueOf(e));
            String answer = "Загрузка файла не удалась! Попробуйте позже снова";
            sendMessage(chatId, answer);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if(isNotAllowedToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
            String answer = "Фото успешно загружено!\n" +
                    "Ссылка для скачивания : " + link;
            sendMessage(chatId, answer);
        } catch (RuntimeException e){
            log.error(String.valueOf(e));
            String answer = "Загрузка фото не удалась! Попробуйте позже снова";
            sendMessage(chatId, answer);
        }
    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        UserStatus userStatus = appUser.getStatus();
        if(!appUser.getIsActive()){
            String error = "Зарегестрируйтесь или авторизируйтесь для загрузки контента!";
            sendMessage(chatId, error);
            return true;
        } else if(!BASIC_STATUS.equals(userStatus)){
            String error = "Отмените текущую комманду с помощью /cancel";
            sendMessage(chatId, error);
            return true;
        }
        return false;
    }

    private String processServiceCommand(AppUser appUser, BotCommands command) {
        if(REGISTRATION.equals(command)){
            return appUserService.registerUser(appUser);
        } else if(HELP.equals(command)){
            return help();
        } else if (START.equals(command)) {
            return "Приветствую! чтобы посмотреть список команд введить /help";
        } else if (CHANGE_MAIL.equals(command)){
            return appUserService.changeEmail(appUser);
        }else {
            return "Неизвестная команда! введите /help для просмотра доступных команд";
        }
    }

    private String help() {
        return """
                Список доступных команд:
                /cancel - отмена выполнения текущей команды;
                /registration - регистрация
                /change_mail - смена email
                """;
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setStatus(BASIC_STATUS);
        appUserRepository.save(appUser);
        return "Команда отменена";
    }

    public void sendMessage(Long chatId, String msg){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(msg);
        answerProducer.produceAnswer(answer);
    }

    public void saveRawData(Update update){
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataRepository.save(rawData);
    }

    public AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> optionalPersistentUser = appUserRepository.findByTelegramUserId(telegramUser.getId());
        if (optionalPersistentUser.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    .isActive(false)
                    .status(BASIC_STATUS)
                    .build();
            appUserRepository.save(transientAppUser);
            return transientAppUser;
        }
        return optionalPersistentUser.get();
    }
}
