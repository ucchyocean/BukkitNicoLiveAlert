/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author ucchy
 * コマンド実行クラス
 */
public class NicoLiveAlertExecutor implements CommandExecutor {

    private NicoLiveAlertPlugin plugin;

    public NicoLiveAlertExecutor(NicoLiveAlertPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * コマンドが実行されたときに呼び出されるメソッド
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("pause") ) {
            plugin.connector.pause();
            sender.sendMessage("Nico Live Alert Plugin was paused.");
            plugin.logger.info("Nico Live Alert Plugin was paused.");
            return true;

        } else if ( args[0].equalsIgnoreCase("start") ) {
            plugin.connector.start();
            sender.sendMessage("Nico Live Alert Plugin was started.");
            plugin.logger.info("Nico Live Alert Plugin was started.");
            return true;

        } else if ( args[0].equalsIgnoreCase("reload") ) {
            try {
                plugin.reloadConfigFile();
            } catch (NicoLiveAlertException e) {
                e.printStackTrace();
            }
            sender.sendMessage("Nico Live Alert Plugin reloaded config.yml.");
            plugin.logger.info("Nico Live Alert Plugin reloaded config.yml.");
            return true;

        }

        return false;
    }

}
