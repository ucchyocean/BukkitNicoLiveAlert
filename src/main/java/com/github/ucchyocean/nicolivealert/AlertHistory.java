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
public class AlertHistory {

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
     * 引数なしのコンストラクタ（外部からのアクセス不可）
     */
    private AlertHistory() {
    }

    /**
     * コンストラクタ
     * @param event 履歴作成元のNicoLiveAlertFoundEvent
     */
    protected AlertHistory(NicoLiveAlertFoundEvent event) {
        this.id = event.getId();
        this.community = event.getCommunity();
        this.user = event.getUser();
        this.title = event.getTitle();
        this.communityName = event.getCommunityName();
        this.communityNickname = event.getCommunityNickname();
        this.userNickname = event.getUserNickname();
        this.startDate = new Date();
    }

    /**
     * 指定されたConfigurationSectionから、AlertHistoryをロードして返す。
     * @param section AlertHistoryが保存されたConfigurationSection
     * @return ロードされたAlertHistory
     */
    protected static AlertHistory loadFromConfigSection(ConfigurationSection section) {
        AlertHistory history = new AlertHistory();
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
