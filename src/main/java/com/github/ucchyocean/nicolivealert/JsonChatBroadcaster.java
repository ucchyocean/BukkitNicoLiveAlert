/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.nicolivealert;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

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
        for ( Player p : getOnlinePlayers() ) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "tellraw " + p.getName() + " " + src);
        }
    }

    /**
     * 現在接続中のプレイヤーを全て取得する
     * @return 接続中の全てのプレイヤー
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Player> getOnlinePlayers() {
        // CB179以前と、CB1710以降で戻り値が異なるため、
        // リフレクションを使って互換性を（無理やり）保つ。
        try {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<?> temp = ((Collection<?>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                return new ArrayList<Player>((Collection<? extends Player>)temp);
            } else {
                Player[] temp = ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                ArrayList<Player> players = new ArrayList<Player>();
                for ( Player t : temp ) {
                    players.add(t);
                }
                return players;
            }
        }
        catch (NoSuchMethodException ex){} // never happen
        catch (InvocationTargetException ex){} // never happen
        catch (IllegalAccessException ex){} // never happen
        return new ArrayList<Player>();
    }
}
