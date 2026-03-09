package com.example.hello.dto;

import lombok.Getter;

/**
 * 绑定推荐人结果，便于前端区分「需绑手机」等
 */
@Getter
public enum BindReferrerResult {
    SUCCESS("绑定成功"),
    USER_PHONE_REQUIRED("需先绑定手机号"),
    REFERRER_PHONE_REQUIRED("推荐人未绑定手机号，暂无法绑定"),
    ALREADY_BOUND("已绑定过推荐人，不可变更"),
    SELF_OR_LOOP("不能绑定自己或自己的下级"),
    REFERRER_NOT_FOUND("推荐人不存在"),
    INVALID_PARAMS("参数无效");

    private final String message;

    BindReferrerResult(String message) {
        this.message = message;
    }
}
