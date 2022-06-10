package uz.pdp.model;

import lombok.Data;
import uz.pdp.bot.enums.BotState;

@Data
public class User {
    private Long id;
    private Long chatId;
    private String firstName = "";
    private String lastName = "";
    private String username = "";
    private String phoneNumber = "";
    private BotState botState;

    public User(Long id, Long chatId) {
        this.id = id;
        this.chatId = chatId;
        botState = BotState.START;
    }
}
