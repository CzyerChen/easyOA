package easyoa.leavemanager.repository.user.custom;

import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.common.domain.vo.VacationCalSearch;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.repository.user.UserVacationCalRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by claire on 2019-08-07 - 09:49
 **/
public class UserVacationCalRepositoryImpl implements UserVacationCalRepositoryCustom {
    @Autowired
    private UserVacationCalRepository userVacationCalRepository;

    @Override
    public Page<UserVacationCal> findPageVacationCalInfo(VacationCalSearch search, Pageable pageable) {
        return userVacationCalRepository.findAll((Specification<UserVacationCal>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(search.getUserIds())){
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("userId"));
                for (int i = 0; i <search.getUserIds().size() ; i++) {
                    in.value(search.getUserIds().get(i));
                }
                Predicate predicate = criteriaBuilder.and(criteriaBuilder.and(in));
                predicateList.add(predicate);
            }
            if(StringUtils.isNotBlank(search.getMonth())){
                LocalDate localDate = DateUtil.parseLocalDateWithAlias(search.getMonth()+"-01");
                predicateList.add(criteriaBuilder.between(root.get("calculateMonth"),localDate,localDate.plusDays(1)));
            }
            if(StringUtils.isNotBlank(search.getUserCode())){
                predicateList.add(criteriaBuilder.equal(root.get("userCode"),search.getUserCode()));
            }
            if(predicateList.size() != 0) {
                Predicate[] predicates = new Predicate[predicateList.size()];
                criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(predicates)));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("calculateMonth")));
                return criteriaQuery.getRestriction();
                //return criteriaBuilder.and(predicateList.toArray(predicates));
            }else{
                return null;
            }

        },pageable);
    }
}
