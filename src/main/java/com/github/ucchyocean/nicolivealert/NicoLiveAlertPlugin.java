/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import java.io.File;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    private static final String KEYWORD_START = "$start";
    private static final String KEYWORD_ELAPSED = "$elapsed";

    private static NicoLiveAlertPlugin instance;

    private NicoLiveConnector connector;
    private NicoLiveAlertConfig config;
    private NicoLiveAlertHistoryContainer history;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // コンフィグのリロード
        reloadNLAConfig();

        // 履歴のロード
        history = NicoLiveAlertHistoryContainer.load();

        // コマンドをサーバーに登録
        getCommand("nicolivealert").setExecutor(new NicoLiveAlertCommand(this));

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

        NicoLiveAlertObject alert = event.getAlert();

        // タイトルキーワードが設定されており、キーワードが見つからない場合は、
        // 通知せずに終了する。一応、ログは出力しておく。
        if ( config.getTitleKeywords().size() > 0 ) {
            boolean keywordFound = false;
            for ( String keyword : config.getTitleKeywords() ) {
                if ( alert.getTitle().contains(keyword) ) {
                    keywordFound = true;
                    break;
                }
            }
            if ( !keywordFound ) {
                getLogger().info("Alert was found, but title didn't contain the keywords. "
                        + alert.getId() + " [" + alert.getTitle() + "]");
                return;
            }
        }

        // 各通知行をキーワードで置き換えして、ブロードキャストに流す。
        String startMessage = replaceKeywords(config.getAlertMessageTemplate(), alert);
        if ( startMessage.length() > 0 ) {
            getServer().broadcastMessage(startMessage);
        }

        String urlMessage = replaceKeywords(config.getAlertURLTemplate(), alert);
        if ( urlMessage.length() > 0 ) {
            String url = String.format(URL_TEMPLATE, alert.getId());
            urlMessage = urlMessage.replace(KEYWORD_URL, url);

            if ( Utility.isCB17orLater() ) {
                broadcastJson(urlMessage);
            } else {
                Bukkit.broadcastMessage(url);
            }
        }

        // 履歴に保存する
        history.addAlert(alert);
    }

    /**
     * プレイヤーがサーバーに接続したときに呼び出されるイベント
     * @param event イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendAlertHistory(event.getPlayer(), false);
    }

    /**
     * ニコ生履歴を指定されたreceiverに送る
     * @param receiver 履歴を表示する対象
     * @param needsNothingAlert 履歴が1つも無い場合にメッセージを送るかどうか。
     */
    public void sendAlertHistory(CommandSender receiver, boolean needsNothingAlert) {

        if ( history.getAlerts().size() <= 0 ) {
            if ( needsNothingAlert ) {
                receiver.sendMessage("NicoLiveAlert history not found.");
            }
            return;
        }

        for ( NicoLiveAlertObject alert : history.getAlerts() ) {
            String message = replaceKeywords(config.getHistoryMessageTemplate(), alert);
            if ( message.length() > 0 ) {
                receiver.sendMessage(message);
            }

            String urlMessage = replaceKeywords(config.getAlertURLTemplate(), alert);
            if ( urlMessage.length() > 0 ) {
                String url = String.format(URL_TEMPLATE, alert.getId());
                urlMessage = urlMessage.replace(KEYWORD_URL, url);

                if ( Utility.isCB17orLater() ) {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "tellraw " + receiver.getName() + " " + urlMessage);
                } else {
                    receiver.sendMessage(url);
                }
            }

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
     * @param alert 置き換えに使用するアラート
     * @return 置き換え後の文字列
     */
    private String replaceKeywords(String source, NicoLiveAlertObject alert) {

        String result = source;
        if ( result.contains("\\n") ) {
            result = result.replace("\\n", "\n");
        }
        if ( result.contains(KEYWORD_COMMUNITY) ) {
            result = result.replace(KEYWORD_COMMUNITY, alert.getCommunityNickname());
        }
        if ( result.contains(KEYWORD_USER) ) {
            result = result.replace(KEYWORD_USER, alert.getUserNickname());
        }
        if ( result.contains(KEYWORD_TITLE) ) {
            result = result.replace(KEYWORD_TITLE, alert.getTitle());
        }
        if ( result.contains(KEYWORD_START) ) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            result = result.replace(KEYWORD_START, format.format(alert.getStartDate()));
        }
        if ( result.contains(KEYWORD_ELAPSED) ) {
            long elapsed = System.currentTimeMillis() - alert.getStartDate().getTime();
            int time = (int)(elapsed / 1000 / 60); // ミリ秒から分に変換
            result = result.replace(KEYWORD_ELAPSED, time + "");
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
