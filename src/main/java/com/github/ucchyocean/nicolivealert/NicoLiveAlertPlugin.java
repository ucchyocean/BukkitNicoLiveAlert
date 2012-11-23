/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ucchy
 * ニコ生アラートプラグイン
 */
public class NicoLiveAlertPlugin extends JavaPlugin {

    private static final String URL_TEMPLATE = "http://live.nicovideo.jp/watch/lv%s";

    protected Logger logger;
    private String urlColor;
    private String urlTemplate;
    private String messageTemplate;
    protected List<String> community;
    protected List<String> user;
    private NicoLiveConnector connector;
    protected Thread connectorThread;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        logger = getLogger();

        try {
            reloadConfigFile();
        } catch (NicoLiveAlertException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // コマンドをサーバーに登録
        getCommand("nicolivealert").setExecutor(new NicoLiveAlertExecutor(this));

        // スレッドを起動してアラートサーバーの監視を開始する
        connect();
    }

    /**
     * プラグインが無効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
        disconnect();
    }

    /**
     * スレッドを起動してアラートサーバーの監視を開始する
     */
    protected boolean connect() {
        if ( connector == null ||
                ( connector != null && connector.isCanceled ) ) {
            connector = new NicoLiveConnector(this);
            connectorThread = new Thread(connector);
            connectorThread.start();
            return true;
        }
        return false;
    }

    /**
     * アラートサーバーとの接続を切断する
     */
    protected boolean disconnect() {
        if ( connector != null ) {
            connector.stop();
            try {
                connectorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connector = null;
            connectorThread = null;
            return true;
        }
        return false;
    }

    /**
     * config.ymlの読み出し処理。
     * @throws NicoLiveAlertException
     */
    protected void reloadConfigFile() throws NicoLiveAlertException {

        File configFile = new File(getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(getFile(), configFile, "config.yml", false);
        }

        reloadConfig();
        FileConfiguration config = getConfig();

        urlColor = config.getString("urlColor", "&c");
        community = config.getStringList("community");
        user = config.getStringList("user");
        String message_temp = config.getString("messageTemplate", "&cニコ生 [%s]で[%s]が開始しました！&r");

        messageTemplate = Utility.replaceColorCode(message_temp);
        urlTemplate = Utility.replaceColorCode(urlColor) + URL_TEMPLATE;
    }

    /**
     * 監視対象の放送が見つかったときに呼び出されるメソッド
     * @param event イベント。見つかった放送の詳細が格納される。
     */
    protected void onAlertFound(AlertFoundEvent event) {

        String startMessage = String.format(messageTemplate, event.communityName, event.title);
        String urlMessage = String.format(urlTemplate, event.id);

        getServer().broadcastMessage(startMessage);
        getServer().broadcastMessage(urlMessage);
    }

}
