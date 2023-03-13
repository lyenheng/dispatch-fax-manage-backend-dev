package com.kedacom.avcs.fax.manage.service;

import com.kedacom.avcs.dispatchfax.client.dto.AttachmentResponseDTO;
import com.kedacom.avcs.fax.manage.data.dto.DispatchFaxManageSendDTO;
import com.kedacom.avcs.fax.manage.data.vo.DispatchFaxManagePageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DispatchFaxManageSendService {

    void sendFax(DispatchFaxManageSendDTO dto);

    void reSendFax(Long id);

    DispatchFaxManagePageVO sendList(String starTime, String endTime, Integer pageNo, Integer pageSize, String searchKey, String status);

    void deleteById(Long id);

    AttachmentResponseDTO uploadFile(MultipartFile file);

    void batchDelete(List<Long> ids);

    String download(Long id);

    void proceedFaxStatus(List list);

    void scannerFaxStatus();
}
