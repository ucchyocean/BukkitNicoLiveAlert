/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.nicolivealert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * ニコ生アラートプラグインの履歴保持クラス
 * @author ucchy
 */
public class NicoLiveAlertHistoryContainer {

    private ArrayList<NicoLiveAlertObject> histories;

    /**
     * コンストラクタ（外部からのアクセス禁止）
     */
    private NicoLiveAlertHistoryContainer() {
        this.histories = new ArrayList<NicoLiveAlertObject>();
    }

    /**
     * 履歴の読出しを行う。
     */
    protected static NicoLiveAlertHistoryContainer load() {

        File dataFolder = NicoLiveAlertPlugin.getInstance().getDataFolder();

        // データフォルダがまだ無いなら、作成する。
        if ( !dataFolder.exists() || !dataFolder.isDirectory() ) {
            dataFolder.mkdirs();
        }

        // コンフィグファイルをロードする。まだ無いなら、作成する。
        File historyFile = new File(dataFolder, "history.yml");
        if ( !historyFile.exists() ) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(historyFile);

        // 履歴を読み込む
        NicoLiveAlertHistoryContainer container = new NicoLiveAlertHistoryContainer();

        for ( String key : config.getKeys(false) ) {
            ConfigurationSection section = config.getConfigurationSection(key);
            NicoLiveAlertObject alert = NicoLiveAlertObject.loadFromConfigSection(section);
            container.histories.add(alert);
        }

        return container;
    }

    /**
     * 履歴の保存を行う。
     */
    private void save() {

        YamlConfiguration config = new YamlConfiguration();

        int index = 1;
        for ( NicoLiveAlertObject alert : histories ) {
            alert.saveIntoConfigSection(config.createSection("alert" + index));
            index++;
        }

        File file = new File(NicoLiveAlertPlugin.getInstance().getDataFolder(), "history.yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * アラートを挿入する。
     * @param alert アラート
     */
    protected void addAlert(NicoLiveAlertObject alert) {

        NicoLiveAlertConfig config = NicoLiveAlertPlugin.getInstance().getNLAConfig();
        int max = config.getAlertHistoryCount();

        // まず、保持されている履歴から期限切れを削除する。
        removeExpiredAlerts();

        // アラートを挿入する。
        histories.add(alert);

        // 最大個数を超えた場合は、先頭から削除する。
        while ( histories.size() > max ) {
            histories.remove(0);
        }

        // 保存する。
        save();
    }

    /**
     * アラートを取得する。
     * @return アラート
     */
    protected ArrayList<NicoLiveAlertObject> getAlerts() {

        // まず、保持されている履歴から期限切れを削除する。削除された場合はいったん保存する。
        if ( removeExpiredAlerts() > 0 ) {
            save();
        }

        // 履歴を複製して返す。
        return new ArrayList<NicoLiveAlertObject>(histories);
    }

    /**
     * 期限切れになっているアラートを履歴から消去する。
     * @return 削除したアラートの個数
     */
    private int removeExpiredAlerts() {

        NicoLiveAlertConfig config = NicoLiveAlertPlugin.getInstance().getNLAConfig();
        int time = config.getAlertHistoryTime();
        long now = System.currentTimeMillis();
        long limit = time * 60 * 1000;

        ArrayList<NicoLiveAlertObject> alertsToRemove = new ArrayList<NicoLiveAlertObject>();
        for ( NicoLiveAlertObject a : histories ) {
            if ( now - a.getStartDate().getTime() > limit ) {
                alertsToRemove.add(a);
            }
        }
        for ( NicoLiveAlertObject a : alertsToRemove ) {
            histories.remove(a);
        }

        return alertsToRemove.size();
    }
}
