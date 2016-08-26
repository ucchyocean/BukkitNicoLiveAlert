/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.nicolivealert;

import java.util.ArrayList;

/**
 * ニコ生アラートプラグインの履歴保持クラス
 * @author ucchy
 */
public class NicoLiveAlertHistoryContainer {

    private ArrayList<AlertHistory> histories;

    /**
     * コンストラクタ（外部からのアクセス禁止）
     */
    private NicoLiveAlertHistoryContainer() {
        this.histories = new ArrayList<AlertHistory>();
    }

    /**
     * 履歴の読出しを行う。
     */
    protected static NicoLiveAlertHistoryContainer load() {

        NicoLiveAlertHistoryContainer container = new NicoLiveAlertHistoryContainer();

        // TODO

        return container;
    }
}
