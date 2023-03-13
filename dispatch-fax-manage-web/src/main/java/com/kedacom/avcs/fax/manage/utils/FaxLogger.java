package com.kedacom.avcs.fax.manage.utils;

import com.kedacom.avcs.dispatch.log.base.constant.LogConstant;
import com.kedacom.avcs.dispatch.log.data.entity.es.OperationLog;
import com.kedacom.avcs.dispatch.log.web.service.LogService;
import com.kedacom.avcs.fax.manage.data.dao.DispatchAvcsFaxInfoDao;
import com.kedacom.avcs.fax.manage.data.dto.DispatchFaxManageSendDTO;
import com.kedacom.avcs.fax.manage.data.entity.DispatchAvcsFaxInfoEntity;
import com.kedacom.ctsp.authz.entity.AuthUser;
import com.kedacom.ctsp.authz.entity.Authentication;
import com.kedacom.ctsp.web.controller.util.ServletUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class FaxLogger {

    @Autowired
    private LogService logService;

    @Autowired
    private DispatchAvcsFaxInfoDao dispatchAvcsFaxInfoDao;

    public void batchDeleteToLogger(List<Long> ids) {
        List<DispatchAvcsFaxInfoEntity> dtos = dispatchAvcsFaxInfoDao.findAllByIdIn(ids);
        OperationLog operationLog = new OperationLog();
        String moudle = "传真管理";
        String content = "";
        if (CollectionUtils.isNotEmpty(dtos)) {
            List<String> subjectAndNameRecv = new ArrayList<>();
            for (DispatchAvcsFaxInfoEntity dto : dtos) {
                subjectAndNameRecv.add(dto.getSubject() + "," + dto.getNameRecv());
            }
            content = String.join(";", subjectAndNameRecv);
        }

        setOperationLoginfo(operationLog, moudle, "批量删除记录", null, content);
        logService.storeDispatchLogAsync(operationLog);
    }

    public void deleteSendByIdToLogger(Long id) {
        DispatchAvcsFaxInfoEntity dto = dispatchAvcsFaxInfoDao.findOne(id);
        OperationLog operationLog = new OperationLog();
        String moudle = "传真管理";
        String content = "";
        if (Objects.nonNull(dto)) {
            content = dto.getSubject() + "," + dto.getNameRecv();
        }
        setOperationLoginfo(operationLog, moudle, "删除记录", null, content);
        logService.storeDispatchLogAsync(operationLog);
    }

    public void reSendFaxToLogger(Long id) {

        DispatchAvcsFaxInfoEntity dto = dispatchAvcsFaxInfoDao.findOne(id);
        OperationLog operationLog = new OperationLog();
        String moudle = "传真管理";
        String content = "";
        if (Objects.nonNull(dto)) {
            content = dto.getSubject() + "," + dto.getNameRecv();
        }
        setOperationLoginfo(operationLog, moudle, "重新发送", null, content);
        logService.storeDispatchLogAsync(operationLog);
    }

    public void sendFaxToLogger(DispatchFaxManageSendDTO dto) {

        OperationLog operationLog = new OperationLog();
        String moudle = "传真管理";
        String content = dto.getSubject() + "," + dto.getNameRecv();
        setOperationLoginfo(operationLog, moudle, "发送传真", null, content);
        logService.storeDispatchLogAsync(operationLog);
    }


    private void setOperationLoginfo(OperationLog operationLog, String moudle, String event, String mpAndMrName, String content) {
        //name
        setDefaultUserInfo(operationLog);
        // IP
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ipAddr = ServletUtil.getIpAddr(request);
        operationLog.setIp(ipAddr);
        //moudle
        operationLog.setMoudle(moudle);
        //event
        operationLog.setEvent(event);
        //mpAndMrName
        operationLog.setMpAndMrNames(mpAndMrName);
        //content
        operationLog.setContent(content);
        //createTime
        operationLog.setCreatedTime(new Date());
    }

    /**
     * 默认设置用户信息的逻辑
     */
    private void setDefaultUserInfo(OperationLog operationLogDTO) {
        Optional<Authentication> current = Authentication.current();
        if (current.isPresent()) {
            AuthUser user = current.get().getUser();
            operationLogDTO.setUsername(user.getUsername());
            operationLogDTO.setName(user.getName());
            operationLogDTO.setCreatedBy(user.getId());
        } else {
            operationLogDTO.setName(LogConstant.ANONYMOUS);
            operationLogDTO.setUsername(LogConstant.ANONYMOUS);
            operationLogDTO.setCreatedBy(LogConstant.ANONYMOUS);
        }
    }
}
