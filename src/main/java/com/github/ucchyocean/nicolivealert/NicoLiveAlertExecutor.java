/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2012-2013
 */
package com.github.ucchyocean.nicolivealert;

import org.bukkit.ChatColor;
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

        if ( args[0].equalsIgnoreCase("disconnect") ) {
            if ( plugin.disconnect() ) {
                sender.sendMessage("Nico Live Alert Plugin was disconnected from alert server.");
                plugin.logger.info("Nico Live Alert Plugin was disconnected from alert server.");
            } else {
                sender.sendMessage("Nico Live Alert Plugin has disconnected already.");
            }
            return true;

        } else if ( args[0].equalsIgnoreCase("connect") ) {
            if ( plugin.connect() ) {
                sender.sendMessage("Nico Live Alert Plugin was connected to alert server.");
                plugin.logger.info("Nico Live Alert Plugin was connected to alert server.");
            } else {
                sender.sendMessage("Nico Live Alert Plugin has connected already.");
            }
            return true;

        } else if ( args[0].equalsIgnoreCase("reload") ) {
            try {
                plugin.reloadConfigFile();
            } catch (NicoLiveAlertException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.DARK_RED + "Nico Live Alert Plugin could not reload config.yml!");
                plugin.logger.info(ChatColor.DARK_RED + "Nico Live Alert Plugin could not reload config.yml!");
                return true;
            }
            sender.sendMessage("Nico Live Alert Plugin reloaded config.yml.");
            plugin.logger.info("Nico Live Alert Plugin reloaded config.yml.");
            return true;

        }

        return false;
    }

}
