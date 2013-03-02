/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.nicolivealert;

/**
 * @author ucchy
 * ニコ生アラートプラグインの例外クラス
 */
public class NicoLiveAlertException extends Exception {
    private static final long serialVersionUID = 92134664493766236L;

    public NicoLiveAlertException(String message) {
        super(message);
    }

    public NicoLiveAlertException(String message, Throwable cause) {
        super(message, cause);
    }
}
