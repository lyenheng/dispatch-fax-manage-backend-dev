package com.kedacom.avcs.fax.manage.task;

import com.kedacom.avcs.fax.manage.service.DispatchFaxManageSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class DispatchFaxQueryStatusTask {


    @Resource
    private DispatchFaxManageSendService dispatchFaxManageSendService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void scannerFaxStatus() {
            dispatchFaxManageSendService.scannerFaxStatus();
    }
}
