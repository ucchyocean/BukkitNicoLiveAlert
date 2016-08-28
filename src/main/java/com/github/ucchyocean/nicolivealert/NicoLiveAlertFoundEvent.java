/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ucchy
 * 放送が見つかったときに投げられるイベントクラス
 */
public class NicoLiveAlertFoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** ニコ生アラートの内容 */
    private NicoLiveAlertObject alert;

    /**
     * コンストラクタ
     * @param alert 見つかったニコ生アラート
     */
    public NicoLiveAlertFoundEvent(NicoLiveAlertObject alert) {
        this.alert = alert;
    }

    /**
     * ハンドラの取得。
     * 本メソッドはCraftBukkitからの呼び出しのために用意しているので、基本的には使用する必要は無い。
     * @see org.bukkit.event.Event#getHandlers()
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * ハンドラの取得。
     * 本メソッドはCraftBukkitからの呼び出しのために用意しているので、基本的には使用する必要は無い。
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * アラートの内容を返す。
     * @return アラート
     */
    public NicoLiveAlertObject getAlert() {
        return alert;
    }
}
