package me.aa07.discordbot.core;

import java.awt.Color;
import java.util.HashMap;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class AACommandListener implements SlashCommandCreateListener {
    private HashMap<String, AACommand> registeredCommands;
    private AABotCore bot;

    public AACommandListener(HashMap<String, AACommand> registeredCommands, AABotCore bot) {
        this.registeredCommands = registeredCommands;
        this.bot = bot;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interact = event.getSlashCommandInteraction();
        if (!registeredCommands.containsKey(interact.getCommandName())) {
            interact.createImmediateResponder().setContent("That command doesnt seem to exist. Please contact affectedarc07.").respond();
            return;
        }

        AACommand aac = registeredCommands.get(interact.getCommandName());
        if (aac.canExecute(interact)) {
            aac.execute(interact);
        } else {
            // Inform on fail
            interact.createImmediateResponder().addEmbed(bot.getEmbed().setTitle("Error").setDescription("Access Denied").setColor(Color.decode("#ff0000"))).respond();
        }
    }

}
