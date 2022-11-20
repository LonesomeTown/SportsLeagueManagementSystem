package com.smu.constant;

/**
 * GameResultEnum
 *
 * @author T.W 11/16/22
 */
public enum GameResultEnum {

    WON(1, "Home Team Won"),
    DRAWN(0, "Drawn"),
    LOST(-1, "Home Team Lost");

    private final int code;
    private final String description;

    GameResultEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
