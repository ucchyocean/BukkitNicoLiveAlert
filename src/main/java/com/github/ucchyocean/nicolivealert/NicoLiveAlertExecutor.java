/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2012
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

    private static final String ADMIN_PERMISSION = "nicolivealert.admin";

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
            // TODO listコマンドの実行
            return true;
        }

        if ( args[0].equalsIgnoreCase("list") ) {
            // TODO listコマンドの実行

            return true;

        } else if ( args[0].equalsIgnoreCase("disconnect") ) {
            if ( !sender.hasPermission(ADMIN_PERMISSION) ) {
                sender.sendMessage("You don't have permission '" + ADMIN_PERMISSION + "'");
                return true;
            }
            if ( plugin.disconnect() ) {
                sender.sendMessage("Nico Live Alert Plugin disconnected from alert server.");
                plugin.getLogger().info("Nico Live Alert Plugin disconnected from alert server.");
            } else {
                sender.sendMessage("Nico Live Alert Plugin has disconnected already.");
            }
            return true;

        } else if ( args[0].equalsIgnoreCase("connect") ) {
            if ( !sender.hasPermission(ADMIN_PERMISSION) ) {
                sender.sendMessage("You don't have permission '" + ADMIN_PERMISSION + "'");
                return true;
            }
            if ( plugin.connect() ) {
                sender.sendMessage("Nico Live Alert Plugin connected to alert server.");
                plugin.getLogger().info("Nico Live Alert Plugin connected to alert server.");
            } else {
                sender.sendMessage("Nico Live Alert Plugin has connected already.");
            }
            return true;

        } else if ( args[0].equalsIgnoreCase("reload") ) {
            if ( !sender.hasPermission(ADMIN_PERMISSION) ) {
                sender.sendMessage("You don't have permission '" + ADMIN_PERMISSION + "'");
                return true;
            }
            plugin.reloadNLAConfig();
            sender.sendMessage("Nico Live Alert Plugin reloaded config.yml.");
            plugin.getLogger().info("Nico Live Alert Plugin reloaded config.yml.");
            return true;

        }

        return false;
    }

}
