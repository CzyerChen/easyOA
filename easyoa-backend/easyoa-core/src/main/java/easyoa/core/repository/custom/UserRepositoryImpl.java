package easyoa.core.repository.custom;

import easyoa.core.repository.UserRepository;
import easyoa.common.domain.vo.UserSearch;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by claire on 2019-06-27 - 11:48
 **/
@Slf4j
public class UserRepositoryImpl implements UserRepositoryCustom {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    /**
     * 最好还是用联合主键做，这样太挫了
     *
     * @param userId
     * @param roleId
     */
    @Override
    public void saveUserRole(long userId, int roleId) {
        String sql = "insert into fb_user_role(user_id,role_id) values(" + userId + "," + roleId + ")";
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            log.error("数据库访问异常：{}", e);
            throw new BussinessException("user_role 插入异常");
        }
    }

    @Override
    public List<Long> findUserIdsForRole(List<Integer> roleId) {
        if (roleId == null || roleId.size() == 0) {
            return null;
        }
        StringBuilder sql = new StringBuilder("select user_id from fb_user_role where role_id in (");
        roleId.forEach(id -> {
            sql.append(id + ",");
        });
        sql.replace(sql.length() - 1, sql.length(), ")");
        log.info("fb_role_menu select sql :{}" + sql);
        Query nativeQuery = entityManager.createNativeQuery(sql.toString());
        return nativeQuery.getResultList();
    }

    @Override
    @Modifying
    @Transactional
    public void deleteUserRole(List<Integer> roleId) {
        //一个用传参的方式，？？？加上参数，，，
        //一个是自己拼SQL，很传统
        //

     /* String sql =   "DELETE FROM fb_user_role WHERE role_id IN (?)";
        String join = StringUtils.join(roleId, ",");
        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1,join);
        nativeQuery.executeUpdate();*/

        String sql = "DELETE FROM fb_user_role  WHERE role_id IN (:roleId)";

        entityManager.createQuery(sql).setParameter("roleId", new ArrayList<Integer>(
                roleId)).executeUpdate();


       /* if (roleId != null && roleId.size() != 0) {
            StringBuilder sql = new StringBuilder("delete from fb_user_role where role_id in (");

            roleId.forEach(id -> {
                sql.append(id + ",");
            });

            sql.replace(sql.length() - 1, sql.length(), ")");
            log.info("fb_role_menu delete sql :{}" + sql);

            Query nativeQuery = entityManager.createNativeQuery(sql.toString());

            nativeQuery.executeUpdate();
        }*/
    }

    @Override
    public void deleteUserRole(Long userId) {
        String sql = "DELETE FROM fb_user_role  WHERE user_id =" + userId;
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            log.error("数据库访问异常：{}", e);
            throw new BussinessException("user_role 插入异常");
        }
    }

    @Override
    public List<User> findBySearchParam(UserSearch userSearch) {
        Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            if (userSearch != null) {
                if (StringUtils.isNotBlank(userSearch.getUsername())) {
                    predicatesList.add(criteriaBuilder.equal(root.get("userName"), userSearch.getUsername()));
                }
                if (null != userSearch.getDeptId()) {
                    predicatesList.add(criteriaBuilder.equal(root.get("deptId"), userSearch.getDeptId()));
                }
                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(userSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(userSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(userSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(userSearch.getCreateTimeTo());
                } else {
                    to = new Date(System.currentTimeMillis() + 3000);
                }
                predicatesList.add(criteriaBuilder.between(root.get("createTime"), from, to));
                predicatesList.add(criteriaBuilder.equal(root.get("deleted"), false));
            }
            if (predicatesList.size() != 0) {
                Predicate[] predicates = new Predicate[predicatesList.size()];
                return criteriaBuilder.and(predicatesList.toArray(predicates));
            } else {
                return null;
            }
        };
        if (specification != null) {
            return userRepository.findAll(specification);
        } else {
            return userRepository.findAll();
        }
    }

    @Override
    public Page<User> findPageBySearchParam(UserSearch userSearch, Pageable pageable) {
        Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            if (userSearch != null) {
                if (StringUtils.isNotBlank(userSearch.getUsername())) {
                    predicatesList.add(criteriaBuilder.like(root.get("userName"), "%"+userSearch.getUsername()+"%"));
                }
                if (null !=userSearch.getCompanyId()) {
                    predicatesList.add(criteriaBuilder.equal(root.get("companyId"), userSearch.getCompanyId()));
                }
                if (null != userSearch.getDeptId()) {
                    predicatesList.add(criteriaBuilder.equal(root.get("deptId"), userSearch.getDeptId()));
                }
                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(userSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(userSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(userSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(userSearch.getCreateTimeTo());
                } else {
                    to = new Date();
                }
                predicatesList.add(criteriaBuilder.between(root.get("createTime"), from, to));
                predicatesList.add(criteriaBuilder.equal(root.get("deleted"), false));
            }
            if(predicatesList.size() != 0) {
                Predicate[] predicates = new Predicate[predicatesList.size()];
                return criteriaBuilder.and(predicatesList.toArray(predicates));
            }else{
                return null;
            }
        };
        if(specification != null){
            return userRepository.findAll(specification, pageable);
        }else{
            return userRepository.findAll(pageable);
        }
    }


}
