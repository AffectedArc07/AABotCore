package me.aa07.botcore.messagecommand;

import java.awt.Color;
import java.util.HashMap;
import java.util.Optional;
import me.aa07.botcore.AABotCore;
import org.javacord.api.event.interaction.MessageContextMenuCommandEvent;
import org.javacord.api.interaction.MessageContextMenuInteraction;
import org.javacord.api.listener.interaction.MessageContextMenuCommandListener;

public class AAMessageCommandListener implements MessageContextMenuCommandListener {
    private HashMap<String, AAMessageCommand> registeredCommands;
    private AABotCore bot;

    public AAMessageCommandListener(HashMap<String, AAMessageCommand> registeredCommands, AABotCore bot) {
        this.registeredCommands = registeredCommands;
        this.bot = bot;
    }

    @Override
    public void onMessageContextMenuCommand(MessageContextMenuCommandEvent event) {
        Optional<MessageContextMenuInteraction> interaction_holder = event.getInteraction().asMessageContextMenuInteraction();
        if (!interaction_holder.isPresent()) {
            return;
        }
        MessageContextMenuInteraction interact = interaction_holder.get();
        if (!registeredCommands.containsKey(interact.getCommandName())) {
            interact.createImmediateResponder().setContent("That command doesnt seem to exist. Please contact affectedarc07.").respond();
            return;
        }

        AAMessageCommand aamc = registeredCommands.get(interact.getCommandName());
        if (aamc.canExecute(interact)) {
            aamc.execute(interact);
        } else {
            // Inform on fail
            interact.createImmediateResponder().addEmbed(bot.getEmbed().setTitle("Error").setDescription("Access Denied").setColor(Color.decode("#ff0000"))).respond();
        }
    }

}
