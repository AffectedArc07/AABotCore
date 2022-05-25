package me.aa07.botcore.slashcommand;

import java.awt.Color;
import java.util.HashMap;
import me.aa07.botcore.AABotCore;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class AASlashCommandListener implements SlashCommandCreateListener {
    private HashMap<String, AASlashCommand> registeredCommands;
    private AABotCore bot;

    public AASlashCommandListener(HashMap<String, AASlashCommand> registeredCommands, AABotCore bot) {
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

        AASlashCommand aasc = registeredCommands.get(interact.getCommandName());
        if (aasc.canExecute(interact)) {
            aasc.execute(interact);
        } else {
            // Inform on fail
            interact.createImmediateResponder().addEmbed(bot.getEmbed().setTitle("Error").setDescription("Access Denied").setColor(Color.decode("#ff0000"))).respond();
        }
    }

}
