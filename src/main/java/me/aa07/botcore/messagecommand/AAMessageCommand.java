package me.aa07.botcore.messagecommand;

import me.aa07.botcore.AABotCore;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.MessageContextMenuBuilder;
import org.javacord.api.interaction.MessageContextMenuInteraction;

public abstract class AAMessageCommand {
    private String commandId;
    private AABotCore bot;

    public AAMessageCommand(String commandId, AABotCore bot) {
        this.commandId = commandId;
        this.bot = bot;
    }

    // Override with your command "work" function. Use canExecute() for permission checking
    public abstract void execute(MessageContextMenuInteraction event);

    // Override where required for if your command requires args
    public void setup(DiscordApi api) {
        new MessageContextMenuBuilder().setName(this.commandId).createGlobal(api).join();
    }

    // Override on child types for if you want custom permissions
    public boolean canExecute(MessageContextMenuInteraction event) {
        return true;
    }

    // Getters
    public String getName() {
        return commandId;
    }

    public AABotCore getBot() {
        return bot;
    }
}
