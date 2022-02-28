package easyoa.core.repository;

import easyoa.core.domain.po.user.Menu;
import easyoa.core.repository.custom.MenuRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:04
 **/
public interface MenuRepository  extends JpaRepository<Menu,Integer>, JpaSpecificationExecutor<Menu>, MenuRepositoryCustom {

    Menu findByMenuId(int menuId);

    List<Menu> findByMenuIdIn(List<Integer> menuIds);

    List<Menu> findByParentIdIn(List<Integer> parentIds);
}
