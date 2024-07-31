package eu.sengy.blockjail.commands;

import eu.sengy.blockjail.utils.Config;
import eu.sengy.blockjail.utils.MMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

public class JailReload extends Command implements TabExecutor {


    public JailReload() {
        super("jailreload");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        return new ArrayList<>();

    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("jail.reload")) {
            Config.load();
            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.reload")));
        }
        else {
            commandSender.sendMessage(MMessage.bungeeConvertToString(Config.getConfig().getString("commands.nopermission")));
        }
    }
}
