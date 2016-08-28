/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.nicolivealert;

import java.util.Date;

import org.bukkit.configuration.ConfigurationSection;

/**
 * ニコ生アラートプラグインの履歴クラス
 * @author ucchy
 */
public class NicoLiveAlertObject {

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

    /** 放送開始時刻 */
    private Date startDate;

    /**
     * コンストラクタ（引数無しコンストラクタは外部からのアクセス禁止）
     */
    private NicoLiveAlertObject() {
    }

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
    protected NicoLiveAlertObject(String id, String community, String user, String title,
            String communityName, String communityNickname, String userNickname) {
        this.id = id;
        this.community = community;
        this.user = user;
        this.title = title;
        this.communityName = communityName;
        this.communityNickname = communityNickname;
        this.userNickname = userNickname;
        this.startDate = new Date();
    }

    /**
     * 指定されたConfigurationSectionから、AlertHistoryをロードして返す。
     * @param section AlertHistoryが保存されたConfigurationSection
     * @return ロードされたAlertHistory
     */
    protected static NicoLiveAlertObject loadFromConfigSection(ConfigurationSection section) {
        NicoLiveAlertObject history = new NicoLiveAlertObject();
        history.id = section.getString("id");
        history.community = section.getString("community");
        history.user = section.getString("user");
        history.title = section.getString("title");
        history.communityName = section.getString("communityName");
        history.communityNickname = section.getString("communityNickname");
        history.userNickname = section.getString("userNickname");
        history.startDate = new Date(section.getLong("startDate"));
        return history;
    }

    /**
     * 指定されたConfigurationSectionに、AlertHistoryを保存する。
     * @param section 保存先のConfigurationSection
     */
    protected void saveIntoConfigSection(ConfigurationSection section) {
        section.set("id", id);
        section.set("community", community);
        section.set("user", user);
        section.set("title", title);
        section.set("communityName", communityName);
        section.set("communityNickname", communityNickname);
        section.set("userNickname", userNickname);
        section.set("startDate", startDate.getTime());
    }

    /**
     * このオブジェクトの文字列表現を返す
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Alert{id=%s,com=%s,title=%s}}", id, community, title);
    }

    // 以下、自動生成のgetterメソッド

    public String getId() {
        return id;
    }

    public String getCommunity() {
        return community;
    }

    public String getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getCommunityName() {
        return communityName;
    }

    public String getCommunityNickname() {
        return communityNickname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public Date getStartDate() {
        return startDate;
    }
}
