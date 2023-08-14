package org.example.service.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BotCommands {
    HELP("/help"),
    CANCEL("/cancel"),
    REGISTRATION("/registration"),
    START("/start");


    private String command;

    @Override
    public String toString() {
        return command;
    }

    public boolean equals(String command){
        return this.toString().equals(command);
    }
}
