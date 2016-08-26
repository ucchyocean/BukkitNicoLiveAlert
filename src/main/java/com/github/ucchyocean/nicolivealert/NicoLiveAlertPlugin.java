/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ucchy
 * ニコ生アラートプラグイン
 */
public class NicoLiveAlertPlugin extends JavaPlugin implements Listener {

    private static final String URL_TEMPLATE = "http://live.nicovideo.jp/watch/lv%s";

    private static final String KEYWORD_COMMUNITY = "$com";
    private static final String KEYWORD_USER = "$user";
    private static final String KEYWORD_TITLE = "$title";
    private static final String KEYWORD_URL = "$url";

    private static NicoLiveAlertPlugin instance;

    private NicoLiveConnector connector;
    private NicoLiveAlertConfig config;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // コンフィグのリロード
        reloadNLAConfig();

        // コマンドをサーバーに登録
        getCommand("nicolivealert").setExecutor(new NicoLiveAlertExecutor(this));

        // 監視イベントを登録
        getServer().getPluginManager().registerEvents(this, this);

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
        if ( connector == null ) {
            connector = new NicoLiveConnector(this);
            connector.runTaskAsynchronously(this);
            return true;
        }
        return false;
    }

    /**
     * アラートサーバーとの接続を切断する
     */
    protected boolean disconnect() {
        if ( connector != null ) {
            connector.cancel();
            connector = null;
            return true;
        }
        return false;
    }

    /**
     * 監視対象の放送が見つかったときに呼び出されるメソッド
     * @param event イベント。見つかった放送の詳細が格納される。
     */
    @EventHandler
    public void onAlertFound(NicoLiveAlertFoundEvent event) {

        // タイトルキーワードが設定されており、キーワードが見つからない場合は、
        // 通知せずに終了する。一応、ログは出力しておく。
        if ( config.getTitleKeywords().size() > 0 ) {
            boolean keywordFound = false;
            for ( String keyword : config.getTitleKeywords() ) {
                if ( event.getTitle().contains(keyword) ) {
                    keywordFound = true;
                    break;
                }
            }
            if ( !keywordFound ) {
                getLogger().info("Alert was found, but title didn't contain the keywords. "
                        + event.getId() + " [" + event.getTitle() + "]");
                return;
            }
        }

        // 各通知行をキーワードで置き換えして、ブロードキャストに流す。
        String startMessage = replaceKeywords(config.getMessageTemplate(), event);
        getServer().broadcastMessage(startMessage);

        if ( !config.getMessageTemplate2().equals("") ) {
            String startMessage2 = replaceKeywords(config.getMessageTemplate2(), event);
            getServer().broadcastMessage(startMessage2);
        }
        if ( !config.getMessageTemplate3().equals("") ) {
            String startMessage3 = replaceKeywords(config.getMessageTemplate3(), event);
            getServer().broadcastMessage(startMessage3);
        }
        if ( !config.getMessageTemplate4().equals("") ) {
            String startMessage4 = replaceKeywords(config.getMessageTemplate4(), event);
            getServer().broadcastMessage(startMessage4);
        }
        if ( !config.getMessageTemplate5().equals("") ) {
            String startMessage5 = replaceKeywords(config.getMessageTemplate5(), event);
            getServer().broadcastMessage(startMessage5);
        }

        String urlMessage = replaceKeywords(config.getMessageTemplateURL(), event);
        String url = String.format(URL_TEMPLATE, event.getId());
        urlMessage = urlMessage.replace(KEYWORD_URL, url);

        if ( Utility.isCB17orLater() ) {
            broadcastJson(urlMessage);
        } else {
            Bukkit.broadcastMessage(url);
        }
    }

    /**
     * JSON-Chat形式のデータをブロードキャストします。
     * @param src JSON-Chat形式のデータ
     */
    public static void broadcastJson(String src) {
        for ( Player p : Utility.getOnlinePlayers() ) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "tellraw " + p.getName() + " " + src);
        }
    }

    /**
     * 文字列の中のキーワードを置き換えするメソッド
     * @param source 置き換え元の文字列
     * @param event 置き換えに使用するイベント
     * @return 置き換え後の文字列
     */
    private String replaceKeywords(String source, NicoLiveAlertFoundEvent event) {

        String result = source;
        if ( result.contains(KEYWORD_COMMUNITY) ) {
            result = result.replace(KEYWORD_COMMUNITY, event.getCommunityNickname());
        }
        if ( result.contains(KEYWORD_USER) ) {
            result = result.replace(KEYWORD_USER, event.getUserNickname());
        }
        if ( result.contains(KEYWORD_TITLE) ) {
            result = result.replace(KEYWORD_TITLE, event.getTitle());
        }
        return result;
    }

    /**
     * NicoLiveAlertのコンフィグを返す
     * @return コンフィグ
     */
    public NicoLiveAlertConfig getNLAConfig() {
        return config;
    }

    /**
     * NicoLiveAlertのコンフィグを再読み込みする
     */
    public void reloadNLAConfig() {
        config = NicoLiveAlertConfig.load();
    }

    /**
     * NicoLiveAlertのインスタンスを返す
     * @return NicoLiveAlert
     */
    public static NicoLiveAlertPlugin getInstance() {
        if ( instance == null ) {
            instance = (NicoLiveAlertPlugin)Bukkit.getPluginManager().getPlugin("NicoLiveAlert");
        }
        return instance;
    }

    /**
     * このプラグインのFileを返す
     * @return File
     */
    public static File getJarFile() {
        return getInstance().getFile();
    }
}
