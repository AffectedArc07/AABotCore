package me.aa07.botcore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import me.aa07.botcore.messagecommand.AAMessageCommand;
import me.aa07.botcore.messagecommand.AAMessageCommandListener;
import me.aa07.botcore.slashcommand.AASlashCommand;
import me.aa07.botcore.slashcommand.AASlashCommandListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageContextMenu;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.listener.GloballyAttachableListener;

public abstract class AABotCore {
    // The logger
    private Logger logger;

    // Map of registered slash commands
    private HashMap<String, AASlashCommand> registeredSlashCommands;

    // Map of registered message commands
    private HashMap<String, AAMessageCommand> registeredMessageCommands;

    // Default footer & colour for embeds
    private String embedFooter;
    private String embedColour;

    private String token;

    // Reference to the discord API object
    private DiscordApi api;

    // Instance to this bot for getting without passthrough
    private static AABotCore instance;

    public AABotCore(String token, String embedfooter, String embedcolour) {
        logger = LogManager.getLogger(AABotCore.class);
        logger.info("Initialised");
        instance = this;
        this.token = token;
        this.embedFooter = embedfooter;
        this.embedColour = embedcolour;

        registeredSlashCommands = new HashMap<String, AASlashCommand>();
        registeredMessageCommands = new HashMap<String, AAMessageCommand>();
    }

    public void launch() {
        api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        logger.info(String.format("Logged in as %s / %s", api.getYourself().getName(), api.getYourself().getId()));

        setupSlashCommands();
        setupMessageCommands();

        logger.info("Registering custom listeners");
        int count = 0;
        for (GloballyAttachableListener gal : getListeners()) {
            count++;
            api.addListener(gal);
            logger.info(String.format("Registered listener %s", gal.getClass().getCanonicalName()));
        }
        logger.info(String.format("Registered %s custom listeners", count));

        // Add the slash listener regardless
        api.addListener(new AASlashCommandListener(registeredSlashCommands, this));
        logger.info("Registered slash listener");

        // And the message one
        api.addListener(new AAMessageCommandListener(registeredMessageCommands, this));
        logger.info("Registered message listener");


        logger.info("Core startup done");
    }

    public abstract ArrayList<AASlashCommand> getSlashCommands();

    public abstract ArrayList<AAMessageCommand> getMessageCommands();

    public abstract ArrayList<GloballyAttachableListener> getListeners();

    public EmbedBuilder getEmbed() {
        return new EmbedBuilder().setFooter(embedFooter).setColor(Color.decode(embedColour));
    }

    public static AABotCore getSelf() {
        return instance;
    }

    public DiscordApi getApi() {
        return api;
    }

    public Logger getLogger() {
        return logger;
    }

    private void setupSlashCommands() {
        logger.info("Registering slash commands...");

        // Get a list of our registered commands
        try {
            Set<SlashCommand> existing_commands = api.getGlobalSlashCommands().get();

            for (AASlashCommand aasc : getSlashCommands()) {
                // First see if our command is in the list already
                boolean skip = false;
                for (SlashCommand sc : existing_commands) {
                    // We exist. Do nothing.
                    if (sc.getName().equals(aasc.getName())) {
                        logger.info(String.format("Slash command %s already exists. Not registering.", aasc.getName()));
                        skip = true;
                        break;
                    }
                }

                registeredSlashCommands.put(aasc.getName(), aasc);

                if (skip) {
                    continue;
                }

                aasc.setup(api);
                logger.info(String.format("Registered slash command %s", aasc.getName()));
            }

            // Cleanup old commands
            for (SlashCommand sc : existing_commands) {
                if (!registeredSlashCommands.keySet().contains(sc.getName())) {
                    logger.info(String.format("Slash command %s does not exist anymore. Removing.", sc.getName()));
                    sc.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info(String.format("Registered %s slash commands", registeredSlashCommands.size()));
    }


    private void setupMessageCommands() {
        logger.info("Registering message commands");

        // Get a list of our registered commands
        try {
            Set<MessageContextMenu> existing_commands = api.getGlobalMessageContextMenus().join();

            for (AAMessageCommand aamc : getMessageCommands()) {
                // First see if our command is in the list already
                boolean skip = false;
                for (MessageContextMenu mcm : existing_commands) {
                    // We exist. Do nothing.
                    if (mcm.getName().equals(aamc.getName())) {
                        logger.info(String.format("Message command %s already exists. Not registering.", aamc.getName()));
                        skip = true;
                        break;
                    }
                }

                registeredMessageCommands.put(aamc.getName(), aamc);

                if (skip) {
                    continue;
                }

                aamc.setup(api);
                logger.info(String.format("Registered command %s", aamc.getName()));
            }

            // Cleanup old commands
            for (MessageContextMenu mcm : existing_commands) {
                if (!registeredMessageCommands.keySet().contains(mcm.getName())) {
                    logger.info(String.format("Message command %s does not exist anymore. Removing.", mcm.getName()));
                    mcm.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info(String.format("Registered %s message commands", registeredMessageCommands.size()));
    }
}
