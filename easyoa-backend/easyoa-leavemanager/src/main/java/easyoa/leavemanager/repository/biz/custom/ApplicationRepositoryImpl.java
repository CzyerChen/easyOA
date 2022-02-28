package easyoa.leavemanager.repository.biz.custom;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.common.domain.vo.ApplicationSearch;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.repository.biz.ApplicationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by claire on 2019-07-19 - 16:52
 **/
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Page<Application> findPageApplicationWithSearchParam(ApplicationSearch applicationSearch, Pageable pageable) {
        List<Predicate> list = new ArrayList<>();
        Specification<Application> specification = (root, criteriaQuery, criteriaBuilder) -> {
            if (applicationSearch != null) {
                if (Objects.nonNull(applicationSearch.getCompanyId())) {
                    list.add(criteriaBuilder.equal(root.get("companyId"), applicationSearch.getCompanyId()));
                }
                if (StringUtils.isNotBlank(applicationSearch.getLeaveType())) {
                    list.add(criteriaBuilder.equal(root.get("leaveType"), applicationSearch.getLeaveType()));
                }
                LocalDateTime from = null;
                LocalDateTime to = null;
                LocalDateTime applyFrom = null;
                LocalDateTime applyTo = null;
                if (StringUtils.isNotBlank(applicationSearch.getCreateTimeFrom())) {
                    LocalDate localDate = DateUtil.parseLocalDateWithAlias(applicationSearch.getCreateTimeFrom());
                    from = LocalDateTime.of(localDate, LocalTime.MIN);
                } else {
                    LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                    from = startOfDay.with(TemporalAdjusters.firstDayOfMonth());
                }
                if (StringUtils.isNotBlank(applicationSearch.getCreateTimeTo())) {
                    LocalDate localDate = DateUtil.parseLocalDateWithAlias(applicationSearch.getCreateTimeTo());
                    to = LocalDateTime.of(localDate, LocalTime.MAX);
                } else {
                    LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                    to = endOfDay.with(TemporalAdjusters.lastDayOfMonth());
                }
                if (StringUtils.isNotBlank(applicationSearch.getApplyTimeFrom())) {
                    LocalDate localDate = DateUtil.parseLocalDateWithAlias(applicationSearch.getApplyTimeFrom());
                    applyFrom = LocalDateTime.of(localDate, LocalTime.MIN);
                } else {
                    LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                    applyFrom = startOfDay.with(TemporalAdjusters.firstDayOfMonth());
                }
                if (StringUtils.isNotBlank(applicationSearch.getApplyTimeTo())) {
                    LocalDate localDate = DateUtil.parseLocalDateWithAlias(applicationSearch.getApplyTimeTo());
                    applyTo = LocalDateTime.of(localDate, LocalTime.MAX);
                } else {
                    LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                    applyTo = endOfDay.with(TemporalAdjusters.lastDayOfMonth());
                }
                if (null != applicationSearch.getUserId()) {
                    list.add(criteriaBuilder.equal(root.get("userId"), applicationSearch.getUserId()));
                }
                if (StringUtils.isNotBlank(applicationSearch.getStatus()) && !"none".equals(applicationSearch.getStatus())) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("status"));
                    for (String status : applicationSearch.getStatus().split(",")) {
                        in.value(status.toUpperCase());
                    }
                    list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
                }
                list.add(criteriaBuilder.between(root.get("createTime"), from, to));
                list.add(criteriaBuilder.between(root.get("startTime"), applyFrom, applyTo));
            }
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
        };
        return applicationRepository.findAll(specification, pageable);
    }
}
