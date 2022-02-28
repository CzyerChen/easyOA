package easyoa.core.repository.custom;

import easyoa.common.domain.vo.MenuSearch;
import easyoa.core.domain.po.user.Menu;

import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-06-26 - 13:54
 **/
public interface MenuRepositoryCustom {
    List<Menu> findMenusByCondition(Map<String,Object> conditions);

    List<Menu> findMenusBySearchParam(MenuSearch menuSearch);
}
