package org.example.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateConsumer {
    void consumeTextMessage(Update update);
    void consumeDocMessage(Update update);
    void consumePhotoMessage(Update update);
}
