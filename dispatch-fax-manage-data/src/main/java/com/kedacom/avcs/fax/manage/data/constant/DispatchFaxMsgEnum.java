package com.kedacom.avcs.fax.manage.data.constant;

import com.kedacom.dispatch.common.data.exception.CodeMessage;
import lombok.Getter;

public enum DispatchFaxMsgEnum implements CodeMessage {
    EFX001("EFX001", "本地传真信息不存在"),
    EFX002("EFX002", "查询日期转换错误"),
    EFX003("EFX003", "推送WebSocket失败"),
    EFX004("EFX004", "字符编码失败"),
    EFX005("EFX005", "传真号码超过70个字符长度限制"),
    ;

    @Getter
    private String code;
    @Getter
    private String message;

    DispatchFaxMsgEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
