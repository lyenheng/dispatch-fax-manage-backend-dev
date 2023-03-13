package com.kedacom.avcs.fax.manage.data.vo;

import lombok.Data;

import java.util.List;

@Data
public class DispatchFaxManagePageVO {

    private Long total;
    private List<DispatchFaxManageListVO> list;
}
