package me.aa07.botcore.slashcommand;

import me.aa07.botcore.AABotCore;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

public abstract class AASlashCommand {
    private String commandId;
    private String description;
    private AABotCore bot;

    public AASlashCommand(String commandId, String description, AABotCore bot) {
        this.commandId = commandId;
        this.description = description;
        this.bot = bot;
    }

    // Override with your command "work" function. Use canExecute() for permission checking
    public abstract void execute(SlashCommandInteraction event);

    // Override where required for if your command requires args
    public void setup(DiscordApi api) {
        SlashCommand.with(getName(), getDesc()).createGlobal(api).join();
    }

    // Override on child types for if you want custom permissions
    public boolean canExecute(SlashCommandInteraction event) {
        return true;
    }

    // Getters
    public String getName() {
        return commandId;
    }

    public String getDesc() {
        return description;
    }

    public AABotCore getBot() {
        return bot;
    }
}
