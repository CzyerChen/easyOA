package easyoa.leavemanager.web;

import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.core.model.TreeNode;
import easyoa.core.service.UserService;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.utils.JWTUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * Created by claire on 2019-06-24 - 11:30
 **/
public class AbstractController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserService userService;

    public String getLocaleMessage(String code, Object... objects) {
        return messageSource.getMessage(code, objects, LocaleContextHolder.getLocale());
    }

    public ApiResponse getResponse(String code, String message, Object data) {
        return ApiResponse.builder()
                .code(Long.valueOf(getLocaleMessage(code)))
                .responseMessage(getLocaleMessage(message))
                .data(data).build();
    }

    public PageRequest getPageRequest(Integer limit, Integer offset, String direction, String orderBy) {
        Sort.Direction d = Sort.Direction.DESC;
        if ("ASC".equalsIgnoreCase(direction)) {
            d = Sort.Direction.ASC;
        }
        PageRequest pageRequest = null;
        if (limit != null && offset != null && StringUtils.isNotBlank(orderBy)) {
            pageRequest = new PageRequest(offset / limit, limit, d, orderBy);
        } else {
            pageRequest = new PageRequest(0, 10);
        }

        return pageRequest;
    }

    public PageRequest getPageRequest(PageRequestEntry entry) {
        PageRequest pageRequest = null;
        if (null != entry.getPageNum() && null != entry.getPageSize()) {
            if (StringUtils.isNotBlank(entry.getSortOrder()) && StringUtils.isNotBlank(entry.getSortField()) && !StringUtils.equals("undefined", entry.getSortField())) {
                pageRequest = new PageRequest(entry.getPageNum() - 1, entry.getPageSize(), "ascend".equalsIgnoreCase(entry.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC, entry.getSortField());
            } else {
                pageRequest = new PageRequest(entry.getPageNum() - 1, entry.getPageSize());
            }
        } else {
            pageRequest = new PageRequest(0, 10);
        }

        return pageRequest;
    }

    public Map<String, Object> getMap(Page<?> values) {
        HashMap<String, Object> map = new HashMap<>();
        if (values == null) {
            map.put("rows", Collections.emptyList());
            map.put("total", 0);
            return map;
        }
        map.put("total", values.getTotalElements());
        if (values.getContent() != null && values.getContent().size() != 0) {
            map.put("rows", values.getContent());
        } else {
            map.put("rows", Collections.emptyList());
        }
        return map;
    }

    public Map<String, Object> getEmptyMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("rows", Collections.emptyList());
        map.put("total", 0);
        return map;
    }


    public Map<String, Object> getMap(List<?> values) {
        HashMap<String, Object> map = new HashMap<>();
        if (values == null) {
            map.put("rows", Collections.emptyList());
            map.put("total", 0);
        } else {
            map.put("total", values.size());
            map.put("rows", values);
        }
        return map;
    }

    public Map<String, Object> getMap(TreeNode<?> values, long size) {
        HashMap<String, Object> map = new HashMap<>();
        if (values == null) {
            map.put("rows", null);
            map.put("total", 0);
        } else {
            map.put("total", size);
            map.put("rows", values);
        }
        return map;
    }

    public Integer getCompanyPermission() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        String username = "";
        if (StringUtils.isNotBlank(token)) {
            username = JWTUtil.getUsername(token);
        }
        Set<String> permsMap = cacheService.getUserPermsSet(username);
        if(!permsMap.contains("data:super")){
            Integer companyId = userService.findCompanyIdByUserName(username);
            if (Objects.isNull(companyId)) {
                return -1;
            } else{
                return companyId;
            }
        }
        return null;
    }
}
