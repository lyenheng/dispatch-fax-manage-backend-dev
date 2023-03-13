package com.kedacom.avcs.fax.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.kedacom.avcs.dispatch.socket.websocket.service.WebSocketService;
import com.kedacom.avcs.dispatchfax.client.DispatchFaxClient;
import com.kedacom.avcs.dispatchfax.client.dto.AttachmentResponseDTO;
import com.kedacom.avcs.dispatchfax.client.dto.DispatchSendFaxDTO;
import com.kedacom.avcs.dispatchfax.client.dto.SendFaxListResponseDTO;
import com.kedacom.avcs.dispatchfax.client.enums.DispatchFaxSendStatusEnum;
import com.kedacom.avcs.fax.manage.data.constant.DispatchFaxConstant;
import com.kedacom.avcs.fax.manage.data.constant.DispatchFaxMsgEnum;
import com.kedacom.avcs.fax.manage.data.dao.DispatchAvcsFaxInfoDao;
import com.kedacom.avcs.fax.manage.data.dto.DispatchFaxManageSendDTO;
import com.kedacom.avcs.fax.manage.data.entity.DispatchAvcsFaxInfoEntity;
import com.kedacom.avcs.fax.manage.data.vo.DispatchFaxManageListVO;
import com.kedacom.avcs.fax.manage.data.vo.DispatchFaxManagePageVO;
import com.kedacom.avcs.fax.manage.service.DispatchFaxManageSendService;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import com.querydsl.core.QueryResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DispatchFaxManageSendServiceImpl implements DispatchFaxManageSendService {

    @Autowired(required = false)
    private DispatchFaxClient dispatchFaxClient;

    @Autowired
    private DispatchAvcsFaxInfoDao dispatchAvcsFaxInfoDao;

    @Autowired
    private WebSocketService webSocketService;

    @Value("${dispatch.fax.name:yfl}")
    String faxName;

    @Override
    public void sendFax(DispatchFaxManageSendDTO dto) {
        DispatchSendFaxDTO sendFaxDTO = new DispatchSendFaxDTO();
        sendFaxDTO.setSubject(dto.getSubject());
        sendFaxDTO.setRecvNames(dto.getNameRecv());
        if(dto.getTelRecv().length() > 70) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX005));
        }
        sendFaxDTO.setRecvTels(dto.getTelRecv());
        sendFaxDTO.setFiles(dto.getFileId());
        if (StringUtils.isNotBlank(dto.getLdc())) {
            sendFaxDTO.setLdc(dto.getLdc());
        }
        List<Integer> ids = null;
        try {
            ids = dispatchFaxClient.sendFax(sendFaxDTO);
        } catch (UnsupportedEncodingException e) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX004));
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            DispatchAvcsFaxInfoEntity faxInfoEntity = new DispatchAvcsFaxInfoEntity();
            faxInfoEntity.setFaxId(ids.get(0));
            faxInfoEntity.setSubject(dto.getSubject());
            faxInfoEntity.setNameRecv(dto.getNameRecv());
            faxInfoEntity.setTelRecv(dto.getTelRecv());
            if (StringUtils.isNotBlank(dto.getLdc())) {
                faxInfoEntity.setLdc(dto.getLdc());
            }
            faxInfoEntity.setFileId(String.valueOf(dto.getFileId()));
            faxInfoEntity.setFileName(dto.getFileName());
            if (faxName.equals("scooper")) {
                faxInfoEntity.setStatus(DispatchFaxSendStatusEnum.SENT_SUCCESSFULLY.getValue());
            } else {
                faxInfoEntity.setStatus(DispatchFaxSendStatusEnum.SENDING.getValue());
            }
            dispatchAvcsFaxInfoDao.save(faxInfoEntity);
        }
    }

    @Override
    public void reSendFax(Long id) {
        DispatchAvcsFaxInfoEntity faxInfoEntity = dispatchAvcsFaxInfoDao.findOne(id);
        if (Objects.isNull(faxInfoEntity)) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX001));
        }
        Integer reSendFax = dispatchFaxClient.reSendFax(faxInfoEntity.getFaxId());
        if (null != reSendFax) {
            faxInfoEntity.setFaxId(reSendFax);
        }
        // 重置发送中状态
        faxInfoEntity.setStatus(DispatchFaxSendStatusEnum.SENDING.getValue());
        dispatchAvcsFaxInfoDao.save(faxInfoEntity);
    }

    @Override
    public AttachmentResponseDTO uploadFile(MultipartFile multipartFile) {
        return dispatchFaxClient.uploadFile(multipartFile);
    }

    @Override
    public DispatchFaxManagePageVO sendList(String starTime, String endTime, Integer pageNo, Integer pageSize, String searchKey, String status) {
        List<Integer> searchStatus = Lists.newArrayList();
        if (Objects.nonNull(status)) {
            if (status.contains(",")) {
                searchStatus = Lists.newArrayList(status.split(",")).stream().map(Integer::valueOf).collect(Collectors.toList());
            } else {
                searchStatus.add(Integer.valueOf(status));
            }
        }
        QueryResults<DispatchAvcsFaxInfoEntity> queryResults = dispatchAvcsFaxInfoDao.findAllByQuery(strToDate(starTime), strToDate(endTime), searchStatus, pageNo, pageSize, searchKey);
        DispatchFaxManagePageVO pageVO = new DispatchFaxManagePageVO();
        pageVO.setTotal(queryResults.getTotal());
        if (CollectionUtils.isNotEmpty(queryResults.getResults())) {
            List<DispatchFaxManageListVO> listVOS = queryResults.getResults()
                    .stream()
                    .map(x -> {
                        DispatchFaxManageListVO faxManageListVO = new DispatchFaxManageListVO();
                        faxManageListVO.setId(x.getId());
                        faxManageListVO.setFaxId(x.getFaxId());
                        if (StringUtils.isNotBlank(x.getLdc())) {
                            faxManageListVO.setLdc(x.getLdc());
                        }
                        faxManageListVO.setFaxNumber(x.getTelRecv());
                        faxManageListVO.setRecipient(x.getNameRecv());
                        faxManageListVO.setSubject(x.getSubject());
                        faxManageListVO.setFileId(x.getFileId());
                        faxManageListVO.setFileName(x.getFileName());
                        faxManageListVO.setStatus(x.getStatus());
                        faxManageListVO.setSendTime(x.getCreatedTime());
                        return faxManageListVO;
                    })
                    .collect(Collectors.toList());
            pageVO.setList(listVOS);
        }
        return pageVO;
    }


    /**
     * 字符串转时间
     */
    private Date strToDate(String dateStr) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            if (StringUtils.isNotBlank(dateStr)) {
                date = sdf.parse(dateStr);
            }
        } catch (ParseException e) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX002));
        }
        return date;
    }

    @Override
    public void deleteById(Long id) {
        DispatchAvcsFaxInfoEntity faxInfoEntity = dispatchAvcsFaxInfoDao.findOne(id);
        if (Objects.isNull(faxInfoEntity)) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX001));
        }
        dispatchFaxClient.deleteSendFax(faxInfoEntity.getFaxId(), Long.valueOf(faxInfoEntity.getFileId()));
        dispatchAvcsFaxInfoDao.delete(faxInfoEntity);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        List<DispatchAvcsFaxInfoEntity> faxInfoList = dispatchAvcsFaxInfoDao.findAllByIdIn(ids);
        if (CollectionUtils.isNotEmpty(faxInfoList)) {
            // 过滤数据
            // 发送中 数据不允许删除
            faxInfoList = faxInfoList
                    .stream()
                    .filter(f ->
                            Objects.nonNull(f) && StringUtils.isNotBlank(f.getFileId()) && Objects.nonNull(f.getFaxId()) &&
                                    Objects.nonNull(f.getStatus()) && !Objects.equals(f.getStatus(), DispatchFaxSendStatusEnum.SENDING.getValue())
                    )
                    .collect(Collectors.toList());

            faxInfoList.forEach(faxInfo -> {
                dispatchFaxClient.deleteSendFax(faxInfo.getFaxId(), Long.valueOf(faxInfo.getFileId()));
            });
            dispatchAvcsFaxInfoDao.deleteInBatch(faxInfoList);
        }
    }

    @Override
    public String download(Long id) {
        DispatchAvcsFaxInfoEntity faxInfoEntity = dispatchAvcsFaxInfoDao.findOne(id);
        if (Objects.isNull(faxInfoEntity)) {
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX001));
        }
        String fileIds = faxInfoEntity.getFileId();
        if (fileIds.contains(";")) {
            // TODO: 2021/6/21 等待批量
            log.info("批量下载");
        }
        return dispatchFaxClient.doDownload(Long.valueOf(fileIds));
    }

    @Override
    public void proceedFaxStatus(List list) {
        // todo 更新数据库中的status
    }

    @Override
    public void scannerFaxStatus() {
        List<DispatchAvcsFaxInfoEntity> faxInfos = dispatchAvcsFaxInfoDao.findAllByStatusEquals(DispatchFaxSendStatusEnum.SENDING.getValue());
        if (CollectionUtils.isEmpty(faxInfos)) {
            return;
        }
        List<Integer> faxIds = faxInfos.parallelStream().map(DispatchAvcsFaxInfoEntity::getFaxId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(faxIds)) {
            log.info("扫描结束，查询三方fax数据={}", JSON.toJSONString(faxInfos));
            faxIds.clear();
            faxInfos.clear();
            return;
        }
        List<SendFaxListResponseDTO> responseDTOS = dispatchFaxClient.queryFaxByIds(faxIds);
        if (CollectionUtils.isNotEmpty(responseDTOS)) {
            Map<Integer, Integer> faxIdToStatusMap = responseDTOS.parallelStream().collect(Collectors.toMap(SendFaxListResponseDTO::getId, SendFaxListResponseDTO::getStatus));
            List<DispatchFaxManageListVO> messageList = Lists.newArrayList();
            DispatchFaxManageListVO faxManageListVO = null;
            for (DispatchAvcsFaxInfoEntity fax : faxInfos) {
                int status = faxIdToStatusMap.getOrDefault(fax.getFaxId(), DispatchFaxSendStatusEnum.SENDING.getValue());
                // status发生变化，添加到通知消息中
                if (!Objects.equals(fax.getStatus(), status)) {
                    faxManageListVO = new DispatchFaxManageListVO();
                    faxManageListVO.setId(fax.getId());
                    faxManageListVO.setFaxId(fax.getFaxId());
                    faxManageListVO.setLdc(fax.getLdc());
                    faxManageListVO.setFaxNumber(fax.getTelRecv());
                    faxManageListVO.setRecipient(fax.getNameRecv());
                    faxManageListVO.setSubject(fax.getSubject());
                    faxManageListVO.setFileId(fax.getFileId());
                    faxManageListVO.setFileName(fax.getFileName());
                    faxManageListVO.setStatus(status);
                    faxManageListVO.setSendTime(fax.getCreatedTime());
                    messageList.add(faxManageListVO);
                }
                fax.setStatus(faxIdToStatusMap.getOrDefault(fax.getFaxId(), DispatchFaxSendStatusEnum.SENDING.getValue()));
                dispatchAvcsFaxInfoDao.save(fax);
            }
            if (CollectionUtils.isNotEmpty(messageList)) {
                sendWebSocket(JSON.toJSONString(messageList));
            }
            faxIdToStatusMap.clear();
            responseDTOS.clear();
            messageList.clear();
        }
        faxIds.clear();
        faxInfos.clear();
    }

    private void sendWebSocket(String msg) {
        // 通知前端，更新
        try {
            log.info("推送web socket：topic= {}，数据{}", DispatchFaxConstant.TOPIC, msg);
            webSocketService.sendWebSocket("", DispatchFaxConstant.TOPIC, msg);
        } catch (Exception e) {
            log.error("推送web socket：topic= {}，数据{}失败！", DispatchFaxConstant.TOPIC, msg, e);
            throw CommonException.error(DispatchMessage.message(DispatchFaxMsgEnum.EFX003));
        }
    }
}
