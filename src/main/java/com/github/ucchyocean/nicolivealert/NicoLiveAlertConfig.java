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
    private String messageTemplate;
    private String messageTemplate2;
    private String messageTemplate3;
    private String messageTemplate4;
    private String messageTemplate5;
    private String messageTemplateURL;
    private HashMap<String, String> communityNicknames;
    private HashMap<String, String> userNicknames;
    private List<String> titleKeywords;
    private int notifyAlertNumOnServerJoin;

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

        conf.messageTemplate = Utility.replaceColorCode(
                config.getString("messageTemplate", "&cニコ生が開始しました！") );
        conf.messageTemplate2 = Utility.replaceColorCode(
                config.getString("messageTemplate2", "&bコミュニティ：$com") );
        conf.messageTemplate3 = Utility.replaceColorCode(
                config.getString("messageTemplate3", "&b放送者：$user") );
        conf.messageTemplate4 = Utility.replaceColorCode(
                config.getString("messageTemplate4", "&b$title") );
        conf.messageTemplate5 = Utility.replaceColorCode(
                config.getString("messageTemplate5", "") );

        conf.messageTemplateURL = config.getString("messageTemplateURL",
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

        conf.notifyAlertNumOnServerJoin = config.getInt("notifyAlertNumOnServerJoin", 3);
        if ( conf.notifyAlertNumOnServerJoin < 0 ) conf.notifyAlertNumOnServerJoin = 0;
        if ( conf.notifyAlertNumOnServerJoin > 10 ) conf.notifyAlertNumOnServerJoin = 10;

        return conf;
    }

    // 以下、自動生成のgetterメソッド。

    public List<String> getCommunity() {
        return community;
    }

    public List<String> getUser() {
        return user;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String getMessageTemplate2() {
        return messageTemplate2;
    }

    public String getMessageTemplate3() {
        return messageTemplate3;
    }

    public String getMessageTemplate4() {
        return messageTemplate4;
    }

    public String getMessageTemplate5() {
        return messageTemplate5;
    }

    public String getMessageTemplateURL() {
        return messageTemplateURL;
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

    public int getNotifyAlertNumOnServerJoin() {
        return notifyAlertNumOnServerJoin;
    }
}
