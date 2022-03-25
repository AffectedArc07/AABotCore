package me.aa07.botcore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.listener.GloballyAttachableListener;

public abstract class AABotCore {
    // The logger
    private Logger logger;

    // Map of registered commands
    private HashMap<String, AACommand> registeredCommands;

    // Default footer & colour for embeds
    private String embedFooter;
    private String embedColour;

    private String token;

    // Reference to the discord API object
    private DiscordApi api;

    // Instance to this bot for getting without passthrough
    private static AABotCore instance;

    public AABotCore(String token, String embedfooter, String embedcolour) {
        // Setup our logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        logger = Logger.getLogger("AABotCore");
        logger.setLevel(Level.ALL);
        logger.info("Initialised");
        instance = this;
        this.token = token;
        this.embedFooter = embedfooter;
        this.embedColour = embedcolour;

        registeredCommands = new HashMap<String, AACommand>();
    }

    public void launch() {
        api = new DiscordApiBuilder().setToken(token).login().join();
        logger.info(String.format("Logged in as %s / %s", api.getYourself().getName(), api.getYourself().getId()));

        logger.info("Registering commands");

        // Get a list of our registered commands
        List<SlashCommand> existing_commands = api.getGlobalSlashCommands().join();

        for (AACommand aac : getCommands()) {
            // First see if our command is in the list already
            boolean skip = false;
            for (SlashCommand sc : existing_commands) {
                // We exist. Do nothing.
                if (sc.getName().equals(aac.getName())) {
                    logger.info(String.format("Command %s already exists. Not registering.", aac.getName()));
                    skip = true;
                    break;
                }
            }

            registeredCommands.put(aac.getName(), aac);

            if (skip) {
                continue;
            }

            aac.setup(api);
            logger.info(String.format("Registered command %s", aac.getName()));
        }

        // Cleanup old commands
        for (SlashCommand sc : existing_commands) {
            if (!registeredCommands.keySet().contains(sc.getName())) {
                logger.info(String.format("Command %s does not exist anymore. Removing.", sc.getName()));
                sc.deleteGlobal();
            }
        }

        logger.info(String.format("Registered %s commands", registeredCommands.size()));

        logger.info("Registering custom listeners");
        int count = 0;
        for (GloballyAttachableListener gal : getListeners()) {
            count++;
            api.addListener(gal);
            logger.info(String.format("Registered listener %s", gal.getClass().getCanonicalName()));
        }
        logger.info(String.format("Registered %s custom listeners", count));

        // Add the slash listener regardless
        api.addListener(new AACommandListener(registeredCommands, this));
        logger.info("Registered slash listener");


        logger.info("Core startup done");
    }

    public abstract ArrayList<AACommand> getCommands();

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
}
