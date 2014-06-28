/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.nicolivealert;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * JSON-Chat形式のデータをブロードキャストで全員に送るクラス。
 * このクラスは Bukkit 1.7.2-R0.2 以前のバージョンでは動作しないことに注意。
 * @author ucchy
 */
public class JsonChatBroadcaster {

    /**
     * JSON-Chat形式のデータをブロードキャストします。
     * @param src JSON-Chat形式のデータ
     */
    public static void broadcastJson(String src) {
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "tellraw " + p.getName() + " " + src);
        }
    }
}
