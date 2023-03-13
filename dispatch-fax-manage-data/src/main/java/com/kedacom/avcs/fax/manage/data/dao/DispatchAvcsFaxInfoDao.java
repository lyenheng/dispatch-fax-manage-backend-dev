package com.kedacom.avcs.fax.manage.data.dao;

import com.kedacom.avcs.fax.manage.data.entity.DispatchAvcsFaxInfoEntity;
import com.kedacom.avcs.fax.manage.data.entity.QDispatchAvcsFaxInfoEntity;
import com.kedacom.kidp.base.data.common.repository.BaseJpaRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public interface DispatchAvcsFaxInfoDao extends BaseJpaRepository<DispatchAvcsFaxInfoEntity> {

    List<DispatchAvcsFaxInfoEntity> findAllByIdIn(List<Long> ids);

    default QueryResults<DispatchAvcsFaxInfoEntity> findAllByQuery(Date startTime, Date endTime, List<Integer> status, Integer pageNumber, Integer pageSize, String search) {
        QDispatchAvcsFaxInfoEntity faxInfo = QDispatchAvcsFaxInfoEntity.dispatchAvcsFaxInfoEntity;
        JPAQuery<DispatchAvcsFaxInfoEntity> jpaQuery = new JPAQueryFactory(this.entityManager()).select(
                Projections.fields(
                        DispatchAvcsFaxInfoEntity.class,
                        faxInfo.id,
                        faxInfo.faxId,
                        faxInfo.fileId,
                        faxInfo.fileName,
                        faxInfo.ldc,
                        faxInfo.telRecv,
                        faxInfo.nameRecv,
                        faxInfo.subject,
                        faxInfo.status,
                        faxInfo.createdTime
                )
        ).from(faxInfo).orderBy(faxInfo.createdTime.desc());
        if (Objects.nonNull(jpaQuery)) {
            if (StringUtils.isNotBlank(search)) {
                jpaQuery.where(faxInfo.telRecv.like("%" + search + "%").or(faxInfo.nameRecv.like("%" + search + "%")).or(faxInfo.subject.like("%" + search + "%")).or(faxInfo.ldc.like("%" + search + "%")));
            }
            if (Objects.nonNull(startTime)) {
                jpaQuery.where(faxInfo.createdTime.goe(startTime));
            }
            if (Objects.nonNull(endTime)) {
                jpaQuery.where(faxInfo.createdTime.loe(endTime));
            }
            if (CollectionUtils.isNotEmpty(status)) {
                jpaQuery.where(faxInfo.status.in(status));
            }
            if (Objects.nonNull(pageNumber)) {
                jpaQuery.offset((long) (pageNumber - 1) * pageSize);
            }
            if (Objects.nonNull(pageSize)) {
                jpaQuery.limit(pageSize);
            }
        }
        return jpaQuery.fetchResults();
    }

    List<DispatchAvcsFaxInfoEntity> findAllByStatusEquals(Integer status);
}
