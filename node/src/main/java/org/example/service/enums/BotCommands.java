package org.example.service.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BotCommands {
    HELP("/help"),
    CANCEL("/cancel"),
    REGISTRATION("/registration"),
    CHANGE_MAIL("/change_mail"),
    START("/start");



    private String command;

    @Override
    public String toString() {
        return command;
    }

    public static BotCommands fromValue(String str){
        for(BotCommands command : BotCommands.values()){
            if(command.toString().equals(str)){
                return command;
            }
        }
        return null;
    }
}
