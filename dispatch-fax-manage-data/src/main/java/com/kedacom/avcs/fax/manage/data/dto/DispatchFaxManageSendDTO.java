package com.kedacom.avcs.fax.manage.data.dto;

import lombok.Data;

@Data
public class DispatchFaxManageSendDTO {

    private Integer faxId;
    private String subject;
    private String ldc;
    // 接收人
    private String telRecv;
    // 接收号码
    private String nameRecv;
    private Integer fileId;
    private String fileName;
}
