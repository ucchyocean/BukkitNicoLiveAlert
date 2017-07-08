/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author ucchy
 * ニコニコ生放送のAPIへの接続クラス
 */
public class NicoLiveConnector extends BukkitRunnable {

    // 取得したchatタグの検索とパース用の正規表現
    private static final String REGEX_CHAT = "<chat [^>]*>([^,]*),([^,]*),([^,]*)</chat>";

    // 前回通知を実行した放送ID
    private static String lastAlertID;

    private Pattern pattern;
    private NicoLiveAlertPlugin plugin;
    private boolean isCanceled;

    /**
     * コンストラクタ。引数に、イベント通知先のNicoLiveAlertPluginを指定する。
     * @param plugin イベント通知先
     */
    public NicoLiveConnector(NicoLiveAlertPlugin plugin) {
        this.plugin = plugin;
        pattern = Pattern.compile(REGEX_CHAT);
        isCanceled = false;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        try {
            String[] alertServerInfo = getAlertServer();
            listen(alertServerInfo);
        } catch (NicoLiveAlertException e) {
            e.printStackTrace();
            isCanceled = true;
        }
    }

    /**
     * NicoLiveConnectorを停止する。
     */
    @Override
    public void cancel() {
        isCanceled = true;
        super.cancel();
    }

    /**
     * ニコニコ生放送のアラートサーバーの接続先を取得する。
     * @return result[0]がホスト名、result[1]がポート番号、result[2]がスレッドID
     * @throws NicoLiveAlertException ネットケーブルが繋がっていない時とか
     */
    private String[] getAlertServer() throws NicoLiveAlertException {
        return getXMLValuesFromURL(
                "http://live.nicovideo.jp/api/getalertinfo",
                new String[]{"addr", "port", "thread"});
    }

    /**
     * getAlertServer() で取得したサーバーに接続し、アラートの監視を行う。
     * @param server getAlertServer() の取得結果を指定する。
     * @throws NicoLiveAlertException サーバーとの接続が切断された時とか
     */
    private void listen(String[] server) throws NicoLiveAlertException {

        String addr = server[0];
        int port = Integer.parseInt(server[1]);
        String thread = server[2];

        Logger logger = plugin.getLogger();

        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            logger.info("Connecting to " + addr + ":" + port + " ... ");

            socket = new Socket(addr, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeBytes("<thread thread=\"" + thread + "\" version=\"20061206\" res_from=\"-1\"/>\0");
            out.flush();

            logger.info("Connected to alert server.");

            int len;
            byte[] buffer = new byte[1024];

            while (0 <= (len = in.read(buffer))) {
                String data = new String(buffer, 0, len);
                Matcher matcher = pattern.matcher(data);
                while ( matcher.find() ) {

                    //plugin.logger.finest(matcher.group(0));

                    NicoLiveAlertConfig config = plugin.getNLAConfig();

                    if ( config.getCommunity().contains(matcher.group(2)) ||
                            config.getUser().contains(matcher.group(3)) ) {
                        // 一致するコミュニティまたはユーザーが見つかった

                        final String id = matcher.group(1);
                        final String community = matcher.group(2);
                        final String user = matcher.group(3);

                        // 前回通知した放送と同じIDなら、無視する。
                        if ( id.equals(lastAlertID) ) {
                            logger.info("Duplicated alert found!(lv" + id +") This alert was ignored.");
                            continue;
                        }
                        lastAlertID = id;

                        // 以降の処理を、非同期処理から同期処理に変更する。
                        new BukkitRunnable() {
                            public void run() {

                                String communityName, title;
                                try {
                                    String[] coNameAndTitle = getCommunityNameAndTitle(id);
                                    communityName = coNameAndTitle[0];
                                    title = coNameAndTitle[1];
                                } catch (NicoLiveAlertException e) {
                                    e.printStackTrace();
                                    communityName = "";
                                    title = "";
                                }

                                String communityNickname = communityName;
                                String userNickname = user;

                                if ( config.getCommunityNicknames() != null
                                        && config.getCommunityNicknames().containsKey(community) ) {
                                    communityNickname = config.getCommunityNicknames().get(community);
                                }
                                if ( config.getUserNicknames() != null
                                        && config.getUserNicknames().containsKey(user) ) {
                                    userNickname = config.getUserNicknames().get(user);
                                }

                                // イベントを作成して、コールする
                                NicoLiveAlertFoundEvent event = new NicoLiveAlertFoundEvent(
                                        new NicoLiveAlertObject(
                                            id, community, user, title,
                                            communityName, communityNickname, userNickname));
                                plugin.getServer().getPluginManager().callEvent(event);

                            }
                        }.runTask(NicoLiveAlertPlugin.getInstance());
                    }
                }

                if ( isCanceled ) {
                    break;
                }
            }

        } catch (UnknownHostException e) {
            throw new NicoLiveAlertException("Error at listening alerts!!", e);
        } catch (IOException e) {
            throw new NicoLiveAlertException("Error at listening alerts!!", e);
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( in != null ) {
                try {
                    in.close();
                } catch (Exception e) {
                    // do nothing.
                }
            }
            if ( socket != null ) {
                try {
                    socket.close();
                } catch (Exception e) {
                    // do nothing.
                }
            }
        }

        logger.info("Disconnected from alert server.");
    }

    /**
     * 引数で指定した放送IDの、コミュニティ名と放送のタイトルを取得する。
     * @param programId 放送ID。lv123456 の lv を抜いた文字列を指定する。
     * @return result[0]がコミュニティ名、result[1]が放送のタイトル
     */
    private String[] getCommunityNameAndTitle(String programId)
            throws NicoLiveAlertException {
        return getXMLValuesFromURL(
                "http://live.nicovideo.jp/api/getstreaminfo/lv" + programId,
                new String[]{"name", "title"});
    }

    /**
     * XMLを返すURLに接続し、targetTagsに指定されたタグから、TextContent(タグの中の文字列)を取得するメソッド
     * @param url 接続先URL
     * @param targetTags 取得するタグ
     * @return targetTagsで指定したタグに入っていた文字列
     * @throws NicoLiveAlertException XMLのパースに失敗したときなど
     */
    private static String[] getXMLValuesFromURL(String url, String[] targetTags) throws NicoLiveAlertException {

        String[] results = new String[targetTags.length];

        HttpURLConnection urlconn = null;

        try {
            URL urlurl = new URL(url);

            urlconn = (HttpURLConnection)urlurl.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
            urlconn.connect();

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(urlconn.getInputStream(), "UTF-8");

            for ( int i=0; i<targetTags.length; i++ ) {
                NodeList list = document.getElementsByTagName(targetTags[i]);
                if ( list.getLength() <= 0 ) {
                    throw new NicoLiveAlertException("Error to get tag " + targetTags[i] + "!");
                }
                results[i] = list.item(0).getTextContent();
            }

            return results;

        } catch (MalformedURLException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } catch (ProtocolException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } catch (DOMException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } catch (IOException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } catch (ParserConfigurationException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } catch (SAXException e) {
            throw new NicoLiveAlertException("Error at parse of responce!", e);
        } finally {
            if ( urlconn != null ) {
                try {
                    urlconn.disconnect();
                } catch (Exception e) {
                    // do nothing.
                }
            }
        }
    }
}
