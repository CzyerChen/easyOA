package easyoa.rulemanager.controller;

import easyoa.common.domain.PageRequestEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by claire on 2019-07-12 - 10:21
 **/
public class AbstractController {

    public PageRequest getPageRequest(PageRequestEntry entry) {
        PageRequest pageRequest = null;
        if (null != entry.getPageNum() && null != entry.getPageSize() ) {
            if(StringUtils.isNotBlank(entry.getDirection())&& StringUtils.isNotBlank(entry.getOrderBy())){
                pageRequest = new PageRequest(entry.getPageNum()-1,entry.getPageSize(),"ASC".equalsIgnoreCase(entry.getDirection())? Sort.Direction.ASC:Sort.Direction.DESC,entry.getOrderBy());
            }else{
                pageRequest = new PageRequest(entry.getPageNum()-1,entry.getPageSize());
            }
        } else {
            pageRequest = new PageRequest(0, 10);
        }

        return pageRequest;
    }


    public Map<String, Object> getMap(Page<?> values) {
        HashMap<String, Object> map = new HashMap<>();
        if (values == null) {
            return null;
        }
        map.put("total", values.getTotalElements());
        if (values.getContent() != null && values.getContent().size() != 0) {
            map.put("rows", values.getContent());
        } else {
            map.put("rows", null);
        }
        return map;
    }
}
