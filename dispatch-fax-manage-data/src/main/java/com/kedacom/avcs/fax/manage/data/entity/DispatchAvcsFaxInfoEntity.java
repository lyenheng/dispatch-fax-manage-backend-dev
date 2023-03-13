package com.kedacom.avcs.fax.manage.data.entity;

import com.kedacom.kidp.base.data.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "avcs_fax_info")
@EntityListeners(AuditingEntityListener.class)
public class DispatchAvcsFaxInfoEntity extends BaseEntity {

    @Basic
    private Integer faxId;
    @Basic
    private String fileId;
    @Basic
    private String fileName;
    //区号
    @Basic
    private String ldc;
    // 收件人号码
    @Basic
    private String telRecv;
    // 收件人名称
    @Basic
    private String nameRecv;
    // 主题
    @Basic
    private String subject;
    // 状态 0：未发送；1:准备中；2：发送中；3：发送成功；4：发送失败。
    @Basic
    private Integer status;
}
