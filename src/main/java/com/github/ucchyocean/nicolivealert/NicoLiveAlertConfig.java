/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.nicolivealert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * ニコ生アラートプラグインコンフィグ
 * @author ucchy
 */
public class NicoLiveAlertConfig {

    private List<String> community;
    private List<String> user;
    private String alertMessageTemplate;
    private String historyMessageTemplate;
    private String alertURLTemplate;
    private HashMap<String, String> communityNicknames;
    private HashMap<String, String> userNicknames;
    private List<String> titleKeywords;
    private int alertHistoryCount;
    private int alertHistoryTime;

    /**
     * コンストラクタ（外部からのアクセス禁止）
     */
    private NicoLiveAlertConfig() {
    }

    /**
     * コンフィグの読出しを行う。
     */
    protected static NicoLiveAlertConfig load() {

        File dataFolder = NicoLiveAlertPlugin.getInstance().getDataFolder();

        // データフォルダがまだ無いなら、作成する。
        if ( !dataFolder.exists() || !dataFolder.isDirectory() ) {
            dataFolder.mkdirs();
        }

        // コンフィグファイルをロードする。まだ無いなら、作成する。
        File configFile = new File(dataFolder, "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(
                    NicoLiveAlertPlugin.getJarFile(), configFile, "config_ja.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // 各種設定を読み込む。
        NicoLiveAlertConfig conf = new NicoLiveAlertConfig();
        conf.community = config.getStringList("community");
        if ( conf.community == null ) {
            conf.community = new ArrayList<String>();
        }
        conf.user = config.getStringList("user");
        if ( conf.user == null ) {
            conf.user = new ArrayList<String>();
        }

        conf.alertMessageTemplate = Utility.replaceColorCode(
                config.getString("alertMessageTemplate",
                        "&cニコ生が開始しました！\n&bコミュニティ：$com\n&b放送者：$user\n&b$title") );

        conf.historyMessageTemplate = Utility.replaceColorCode(
                config.getString("historyMessageTemplate",
                        "&cニコ生 $elapsed分経過\n&b$title") );

        conf.alertURLTemplate = config.getString("alertURLTemplate",
                "{\"text\":\"＞放送ページはこちら！＜\","
                + "\"color\":\"red\",\"underlined\":\"true\",\"clickEvent\":{"
                + "\"action\":\"open_url\",\"value\":\"$url\"}}");

        conf.communityNicknames = new HashMap<String, String>();
        if ( config.contains("communityNicknames") ) {
            ConfigurationSection section =
                    config.getConfigurationSection("communityNicknames");
            for ( String key : section.getKeys(false) ) {
                conf.communityNicknames.put(key, section.getString(key));
            }
        }

        conf.userNicknames = new HashMap<String, String>();
        if ( config.contains("userNicknames") ) {
            ConfigurationSection section =
                    config.getConfigurationSection("userNicknames");
            for ( String key : section.getKeys(false) ) {
                conf.userNicknames.put(key, section.getString(key));
            }
        }

        conf.titleKeywords = config.getStringList("titleKeywords");
        if ( conf.titleKeywords == null ) {
            conf.titleKeywords = new ArrayList<String>();
        }

        conf.alertHistoryCount = config.getInt("alertHistoryCount", 3);
        if ( conf.alertHistoryCount < 0 ) conf.alertHistoryCount = 0;
        if ( conf.alertHistoryCount > 10 ) conf.alertHistoryCount = 10;

        conf.alertHistoryTime = config.getInt("alertHistoryTime", 360);
        if ( conf.alertHistoryTime < 0 ) conf.alertHistoryTime = 0;
        if ( conf.alertHistoryTime > 1440 ) conf.alertHistoryTime = 1440;

        return conf;
    }

    // 以下、自動生成のgetterメソッド。

    public List<String> getCommunity() {
        return community;
    }

    public List<String> getUser() {
        return user;
    }

    public String getAlertMessageTemplate() {
        return alertMessageTemplate;
    }

    public String getHistoryMessageTemplate() {
        return historyMessageTemplate;
    }

    public String getAlertURLTemplate() {
        return alertURLTemplate;
    }

    public HashMap<String, String> getCommunityNicknames() {
        return communityNicknames;
    }

    public HashMap<String, String> getUserNicknames() {
        return userNicknames;
    }

    public List<String> getTitleKeywords() {
        return titleKeywords;
    }

    public int getAlertHistoryCount() {
        return alertHistoryCount;
    }

    public int getAlertHistoryTime() {
        return alertHistoryTime;
    }
}
