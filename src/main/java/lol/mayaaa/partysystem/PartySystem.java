package lol.mayaaa.partysystem;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lol.mayaaa.partysystem.commands.PartyCommand;
import lol.mayaaa.partysystem.managers.PartyManager;
import org.slf4j.Logger;

@Plugin(
        id = "partysystem",
        name = "PartySystem",
        version = "1.0"
)
public class PartySystem {

    private static PartySystem instance;
    private PartyManager partyManager;

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private CommandManager commandManager;

    public static PartySystem getInstance() {
        return instance;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        partyManager = new PartyManager();

        // registering the /party cmd [/p is alias]
        CommandMeta partyMeta = commandManager.metaBuilder("party")
                .aliases("p")
                .plugin(this)
                .build();

        commandManager.register(partyMeta, new PartyCommand());

        logger.info("PartySystem has been enabled!");
    }
}