/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.nicolivealert;

import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * JSON-Chat形式のデータをブロードキャストで全員に送るクラス。
 * このクラスは CB-1.7.2-R0.x における代替策として導入するので、
 * 該当バージョン以外では動作しないようにする必要がある。
 * @author ucchy
 */
public class JsonChatBroadcasterV17R1 {

    /**
     * JSON-Chat形式のデータをブロードキャストします。
     * @param src JSON-Chat形式のデータ
     */
    public static void broadcastJson(String src) {

        IChatBaseComponent comp = ChatSerializer.a(src);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
