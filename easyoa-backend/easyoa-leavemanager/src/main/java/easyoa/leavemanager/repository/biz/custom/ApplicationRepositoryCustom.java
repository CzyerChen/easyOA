package easyoa.leavemanager.repository.biz.custom;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.common.domain.vo.ApplicationSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by claire on 2019-07-19 - 16:51
 **/
public interface ApplicationRepositoryCustom {
    Page<Application> findPageApplicationWithSearchParam(ApplicationSearch applicationSearch, Pageable pageable);

}
