package com.push.model;

import lombok.Getter;

/**
 * push result .
 *
 * @author itning
 * @since 2021/3/22 16:58
 */
@Getter
public class PushResult {

    private final boolean success;
    private String result;

    public PushResult(boolean success) {
        this.success = success;
    }

    public PushResult(boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public static PushResult success() {
        return new PushResult(true);
    }

    public static PushResult success(String result) {
        return new PushResult(true, result);
    }

    public static PushResult failed() {
        return new PushResult(false);
    }

    public static PushResult failed(String result) {
        return new PushResult(false, result);
    }

    public static boolean checkSuccess(PushResult pushResult) {
        return null != pushResult && pushResult.isSuccess();
    }
}
