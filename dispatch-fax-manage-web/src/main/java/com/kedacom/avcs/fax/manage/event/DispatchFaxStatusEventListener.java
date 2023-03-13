package com.kedacom.avcs.fax.manage.event;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.kedacom.avcs.dispatch.message.center.annotations.AvcsMessageHandleAno;
import com.kedacom.avcs.dispatch.message.center.data.AvcsConsumerData;
import com.kedacom.avcs.dispatch.message.center.data.Message;
import com.kedacom.avcs.dispatch.message.center.service.AvcsConsumerDataEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AvcsMessageHandleAno(
        topics = "${dispatch.fax.topics:DISPATCH_FAX_NOTIFY}"
)
public class DispatchFaxStatusEventListener implements AvcsConsumerDataEventService {

    @Override
    public void getMessage(AvcsConsumerData avcsConsumerData) {
        if (avcsConsumerData != null && CollectionUtils.isNotEmpty(avcsConsumerData.getMessage())) {
            proceedFaxStatus(avcsConsumerData.getMessage());
        }
    }

    private void proceedFaxStatus(List<Message> messageList) {
        //List<DispatchFaxNotificationDTO> faxNotificationDTOList = Lists.newArrayList();
        //messageList.stream()
        //        .filter(x -> !StringUtil.isNullOrEmpty(x.getData()))
        //        .forEach(x -> {
        //            try {
        //                DispatchFaxNotificationDTO callback = JSON.parseObject(x.getData(), DispatchFaxNotificationDTO.class);
        //                if (callback != null && StringUtils.isNotBlank(callback.getFaxId())) {
        //                    faxNotificationDTOList.add(callback);
        //                }
        //            } catch (Exception e) {
        //                log.info("转换设备数据：{}失败！", x.getData(), e);
        //            }
        //        });
        //if (CollectionUtils.isNotEmpty(faxNotificationDTOList)) {
        //    // 处理数据更新
        //}
    }
}
