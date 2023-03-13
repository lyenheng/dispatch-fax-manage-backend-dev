package com.kedacom.avcs.fax.manage.data.vo;

import com.kedacom.avcs.dispatchfax.client.enums.DispatchFaxSendStatusEnum;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import java.util.Date;

@Data
@SuppressFBWarnings
public class DispatchFaxManageListVO {

    private Long id;
    private Integer faxId;
    private String subject;
    private String recipient;
    private String ldc;
    private String faxNumber;
    private String fileId;
    private String fileName;
    private Integer status = DispatchFaxSendStatusEnum.NOT_SEND.getValue();
    private Date sendTime;
}
