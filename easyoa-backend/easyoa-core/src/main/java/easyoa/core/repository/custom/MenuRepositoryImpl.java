package easyoa.core.repository.custom;

import easyoa.core.repository.MenuRepository;
import easyoa.common.domain.vo.MenuSearch;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 13:56
 **/
public class MenuRepositoryImpl implements MenuRepositoryCustom {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MenuRepository menuRepository;

    @Override
    public List<Menu> findMenusByCondition(Map<String, Object> conditions) {
        Specification<Menu> specification = (Specification<Menu>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            for (Map.Entry<String, Object> condition : conditions.entrySet()) {
                predicatesList.add(criteriaBuilder.equal(root.get(condition.getKey()), condition.getValue()));
            }
            if (predicatesList.size() != 0) {
                Predicate[] predicates = new Predicate[predicatesList.size()];
                return criteriaBuilder.and(predicatesList.toArray(predicates));
            } else {
                return null;
            }
        };
        if (specification != null) {
            return menuRepository.findAll(specification);
        } else {
            return menuRepository.findAll();
        }
    }

    @Override
    public List<Menu> findMenusBySearchParam(MenuSearch menuSearch) {
        Specification<Menu> specification = (Specification<Menu>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (menuSearch != null) {
                if (StringUtils.isNotBlank(menuSearch.getMenuName())) {
                    list.add(criteriaBuilder.like(root.get("menuName"), '%' + menuSearch.getMenuName() + '%'));
                }
                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(menuSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(menuSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(menuSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(menuSearch.getCreateTimeTo());
                } else {
                    to = new Date(System.currentTimeMillis() + 3000);
                }
                list.add(criteriaBuilder.between(root.get("createTime"), from, to));
            }
            if (list.size() != 0) {
                Predicate[] predicates = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(predicates));

            } else {
                return null;
            }
        };
        if (specification != null) {
            return menuRepository.findAll(specification);
        } else {
            return menuRepository.findAll();
        }
    }
}
