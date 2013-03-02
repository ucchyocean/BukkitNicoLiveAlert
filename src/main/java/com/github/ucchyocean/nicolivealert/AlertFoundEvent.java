/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

/**
 * @author ucchy
 * 放送が見つかったときに投げられるイベントクラス
 */
public class AlertFoundEvent {

    /** 放送ID。lv123456 の lv を抜いた文字列になる。 */
    protected String id;
    /** コミュニティID。co12345 みたいな感じの文字列になる。 */
    protected String community;
    /** ユーザーID。 */
    protected String user;

    /** 放送タイトル */
    protected String title;
    /** 放送しているコミュニティ名 */
    protected String communityName;

    /** コミュニティニックネーム */
    protected String communityNickname;
    /** ユーザーニックネーム */
    protected String userNickname;
}
