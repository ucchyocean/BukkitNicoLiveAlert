/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2012-2013
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

    /** 放送ID。lv123456 の lv を抜いた文字列になる。 */
    private String id;
    /** コミュニティID。co12345 みたいな感じの文字列になる。 */
    private String community;
    /** ユーザーID。 */
    private String user;

    /** 放送タイトル */
    private String title;
    /** 放送しているコミュニティ名 */
    private String communityName;

    /** コミュニティニックネーム */
    private String communityNickname;
    /** ユーザーニックネーム */
    private String userNickname;

    /**
     * コンストラクタ
     * @param id 放送ID
     * @param community コミュニティID
     * @param user ユーザーID
     * @param title 放送タイトル
     * @param communityName 放送しているコミュニティ名
     * @param communityNickname コミュニティニックネーム
     * @param userNickname ユーザーニックネーム
     */
    public NicoLiveAlertFoundEvent(String id, String community, String user, String title,
            String communityName, String communityNickname, String userNickname) {
        this.id = id;
        this.community = community;
        this.user = user;
        this.title = title;
        this.communityName = communityName;
        this.communityNickname = communityNickname;
        this.userNickname = userNickname;
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
     * @return 放送IDを返す
     */
    public String getId() {
        return id;
    }

    /**
     * @return コミュニティIDを返す
     */
    public String getCommunity() {
        return community;
    }

    /**
     * @return ユーザーIDを返す
     */
    public String getUser() {
        return user;
    }

    /**
     * @return 放送タイトルを返す
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return 放送しているコミュニティ名を返す
     */
    public String getCommunityName() {
        return communityName;
    }

    /**
     * @return コミュニティニックネームを返す
     */
    public String getCommunityNickname() {
        return communityNickname;
    }

    /**
     * @return ユーザーニックネームを返す
     */
    public String getUserNickname() {
        return userNickname;
    }

}
