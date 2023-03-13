package com.kedacom.avcs.fax.manage.controller;

import com.kedacom.avcs.fax.manage.data.dto.DispatchFaxManageSendDTO;
import com.kedacom.avcs.fax.manage.service.DispatchFaxManageSendService;
import com.kedacom.avcs.fax.manage.utils.FaxLogger;
import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/faxManage/send")
@Slf4j
@Api(tags = "传真管理")
public class DispatchFaxManageSendController {

    @Autowired
    private DispatchFaxManageSendService dispatchFaxManageSendService;

    @Autowired
    private FaxLogger faxLogger;

    @ApiOperation("发送传真")
    @PostMapping("/sendFax")
    public ResponseMessage sendFax(@RequestBody DispatchFaxManageSendDTO dto) {
        faxLogger.sendFaxToLogger(dto);
        dispatchFaxManageSendService.sendFax(dto);
        return ResponseMessage.ok();
    }

    @ApiOperation("重新发送传真")
    @GetMapping("/reSendFax/{id}")
    public ResponseMessage reSendFax(@PathVariable("id") Long id) {
        faxLogger.reSendFaxToLogger(id);
        dispatchFaxManageSendService.reSendFax(id);
        return ResponseMessage.ok();
    }

    @ApiOperation("上传附件")
    @PostMapping("/uploadFile")
    public ResponseMessage uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseMessage.ok(dispatchFaxManageSendService.uploadFile(file));
    }

    @ApiOperation("发件箱分页查询")
    @GetMapping("/sendList")
    public ResponseMessage sendList(@RequestParam(name = "startTime", required = false) String starTime,
                                    @RequestParam(name = "endTime", required = false) String endTime,
                                    @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                    @RequestParam(name = "searchKey", required = false) String searchKey,
                                    @RequestParam(name = "status", required = false) String status) {
        return ResponseMessage.ok(dispatchFaxManageSendService.sendList(starTime, endTime, pageNo, pageSize, searchKey, status));
    }

    @ApiOperation("根据id删除发件信息")
    @DeleteMapping("/{id}")
    public ResponseMessage deleteSendById(@PathVariable("id") Long id) {
        faxLogger.deleteSendByIdToLogger(id);
        dispatchFaxManageSendService.deleteById(id);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据id删除发件信息")
    @PostMapping("/batch/delete")
    public ResponseMessage batchDelete(@RequestBody List<Long> ids) {
        faxLogger.batchDeleteToLogger(ids);
        dispatchFaxManageSendService.batchDelete(ids);
        return ResponseMessage.ok();
    }

    @ApiOperation("根据id下载附件")
    @GetMapping("/download/{id}")
    public ResponseMessage download(@PathVariable("id") Long id) {
        return ResponseMessage.ok(dispatchFaxManageSendService.download(id));
    }
}
