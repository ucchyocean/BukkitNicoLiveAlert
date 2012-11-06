/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author ucchy
 * ニコニコ生放送のAPIへの接続クラス
 */
public class NicoLiveConnector implements Runnable {

    // 取得したchatタグの検索とパース用の正規表現
    private static final String REGEX_CHAT = "<chat [^>]*>([^,]*),([^,]*),([^,]*)</chat>";

    private Pattern pattern;
    private String addr;
    private int port;
    private String thread;
    private NicoLiveAlertPlugin plugin;
    private boolean isCanceled;
    private boolean isPaused;

    /**
     * コンストラクタ。引数に、イベント通知先のNicoLiveAlertPluginを指定する。
     * @param plugin イベント通知先
     */
    public NicoLiveConnector(NicoLiveAlertPlugin plugin) {
        this.plugin = plugin;
        pattern = Pattern.compile(REGEX_CHAT);
        isCanceled = false;
        isPaused = false;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        try {
            getAlertServer();
            startListen();
        } catch (NicoLiveAlertException e) {
            e.printStackTrace();
        }
    }

    /**
     * NicoLiveConnectorを停止する。すぐには停止できないので、このメソッドを呼んだ後に、スレッドにjoinすること。
     */
    public void stop() {
        isCanceled = true;
    }

    /**
     * NicoLiveConnectorを一時停止する。
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * 一時停止していたNicoLiveConnectorを再開する。
     */
    public void start() {
        isPaused = false;
    }

    /**
     * ニコニコ生放送のアラートサーバーの接続先を取得する。
     * @throws NicoLiveAlertException ネットケーブルが繋がっていない時とか
     */
    private void getAlertServer() throws NicoLiveAlertException {

        HttpURLConnection urlconn = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("http://live.nicovideo.jp/api/getalertinfo");

            urlconn = (HttpURLConnection)url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();

            reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(urlconn.getInputStream(), "UTF-8");
            addr = document.getElementsByTagName("addr").item(0).getTextContent();
            String port_temp = document.getElementsByTagName("port").item(0).getTextContent();
            thread = document.getElementsByTagName("thread").item(0).getTextContent();

            port = Integer.parseInt(port_temp);

        } catch (MalformedURLException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } catch (ProtocolException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } catch (DOMException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } catch (IOException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } catch (ParserConfigurationException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } catch (SAXException e) {
            throw new NicoLiveAlertException("Error at getting alert server!", e);
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( urlconn != null ) {
                try {
                    urlconn.disconnect();
                } catch (Exception e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * getAlertServer() で取得したサーバーに接続し、アラートの監視を行う。
     * @throws NicoLiveAlertException サーバーとの接続が切断された時とか
     */
    private void startListen() throws NicoLiveAlertException {

        try {
            plugin.logger.info("Connecting to " + addr + ":" + port + " ... ");

            Socket socket = new Socket(addr, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeBytes("<thread thread=\"" + thread + "\" version=\"20061206\" res_from=\"-1\"/>\0");
            out.flush();

            plugin.logger.info("Connected. Starting to listen.");

            int len;
            byte[] buffer = new byte[1024];

            while (0 <= (len = in.read(buffer))) {
                String data = new String(buffer, 0, len);
                Matcher matcher = pattern.matcher(data);
                while ( matcher.find() ) {

                    if ( !isPaused && (
                            plugin.community.contains(matcher.group(2)) ||
                            plugin.community.contains(matcher.group(3)) ) ) {

                        AlertFoundEvent event = new AlertFoundEvent();
                        event.id = matcher.group(1);
                        event.community = matcher.group(2);
                        event.user = matcher.group(3);

                        String[] coNameAndTitle = getCommunityNameAndTitle(event.id);
                        event.communityName = coNameAndTitle[0];
                        event.title = coNameAndTitle[1];

                        plugin.onAlertFound(event);
                    }
                }

                if ( isCanceled ) {
                    break;
                }
            }

        } catch (UnknownHostException e) {
            throw new NicoLiveAlertException("Error at starting listen!", e);
        } catch (IOException e) {
            throw new NicoLiveAlertException("Error at starting listen!", e);
        }
    }

    /**
     * 引数で指定した放送IDの、コミュニティ名と放送のタイトルを取得する。
     * @param programId 放送ID。lv123456 の lv を抜いた文字列を指定する。
     * @return result[0]がコミュニティ名、result[1]が放送のタイトル
     */
    private static String[] getCommunityNameAndTitle(String programId) {

        String[] result = new String[2];
        String community = "";
        String title = "";

        try {
            URL url = new URL("http://live.nicovideo.jp/api/getstreaminfo/lv" + programId);

            HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
            urlconn.connect();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(urlconn.getInputStream()));

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(urlconn.getInputStream(), "UTF-8");
            community = document.getElementsByTagName("name").item(0).getTextContent();
            title = document.getElementsByTagName("title").item(0).getTextContent();

            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (DOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        result[0] = community;
        result[1] = title;
        return result;
    }
}
