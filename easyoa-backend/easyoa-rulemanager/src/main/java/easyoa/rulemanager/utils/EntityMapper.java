package easyoa.rulemanager.utils;

import easyoa.common.domain.vo.LeaveRuleVO;
import easyoa.common.utils.DateUtil;
import easyoa.rulemanager.domain.LeaveRules;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-11 - 17:42
 **/
public class EntityMapper {

    public static List<LeaveRuleVO> rules2VO(List<LeaveRules> list) {
        if(list != null){
           return list.stream().map(rule -> {
               LeaveRuleVO vo = new LeaveRuleVO();
               vo.setRuleId(rule.getRuleId());
               if(StringUtils.isNotEmpty(rule.getRuleName())){
                   vo.setRuleName(rule.getRuleName());
               }
               if(null!=rule.getRuleType()){
                   vo.setRuleType(rule.getRuleType());
               }

               if(null!=rule.getSubType()){
                   vo.setSubType(rule.getSubType());
               }
               if(null!=rule.getLeaveDaysFrom()){
                   vo.setLeaveDaysFrom(rule.getLeaveDaysFrom());
               }
               if(null!=rule.getLeaveDaysTo()){
                   vo.setLeaveDaysTo(rule.getLeaveDaysTo());
               }
               if(null!=rule.getMaxPermitDay()){
                   vo.setMaxPermitDay(rule.getMaxPermitDay());
               }
               if(null!=rule.getForwardDays()){
                   vo.setForwardDays(rule.getForwardDays());
               }
               if(null!=rule.getAge()){
                   vo.setAge(rule.getAge());
               }
               if(null!=rule.getWorkYearsFrom()){
                   vo.setWorkYearsFrom(rule.getWorkYearsFrom());
               }
               if(null!=rule.getWorkYearsTo()){
                   vo.setWorkYearsTo(rule.getWorkYearsTo());
               }
               if(null!=rule.getNeedUpload()){
                   vo.setNeedUpload(rule.getNeedUpload());
               }
               if(StringUtils.isNotEmpty(rule.getNotice())){
                   vo.setNotice(rule.getNotice());
               }
               if(StringUtils.isNotEmpty(rule.getFileRequired())){
                   vo.setFileRequired(rule.getFileRequired());
               }
               if(null != rule.getCreateTime()){
                   vo.setCreateTime(DateUtil.formatDate(rule.getCreateTime(),DateUtil.DATE_PATTERN_WITH_HYPHEN));
               }
               if(null != rule.getUpdateTime()){
                   vo.setUpdateTime(DateUtil.formatDate(rule.getUpdateTime(),DateUtil.DATE_PATTERN_WITH_HYPHEN));
               }
               vo.setCompanyId(rule.getCompanyId());
               return vo;
           }).collect(Collectors.toList());
        }
        return null;
    }


    public static LeaveRules vo2Rules(LeaveRuleVO vo){
        if(vo != null) {
            LeaveRules r = new LeaveRules();
            r.setRuleId(vo.getRuleId());
            if (StringUtils.isNotBlank(vo.getRuleName())) {
                r.setRuleName(vo.getRuleName());
            }
            if (null != vo.getRuleType()) {
                r.setRuleType(vo.getRuleType());
            }

            if (null != vo.getSubType()) {
                r.setSubType(vo.getSubType());
            }
            if (null != vo.getLeaveDaysFrom()) {
                r.setLeaveDaysFrom(vo.getLeaveDaysFrom());
            }
            if (null != vo.getLeaveDaysTo()) {
                r.setLeaveDaysTo(vo.getLeaveDaysTo());
            }
            if (null != vo.getMaxPermitDay()) {
                r.setMaxPermitDay(vo.getMaxPermitDay());
            }
            if (null != vo.getForwardDays()) {
                r.setForwardDays(vo.getForwardDays());
            }
            if (null != vo.getAge()) {
                r.setAge(vo.getAge());
            }
            if (null != vo.getWorkYearsFrom()) {
                r.setWorkYearsFrom(vo.getWorkYearsFrom());
            }
            if (null != vo.getWorkYearsTo()) {
                r.setWorkYearsTo(vo.getWorkYearsTo());
            }
            if (null != vo.getNeedUpload()) {
                r.setNeedUpload(vo.getNeedUpload());
            }
            if (StringUtils.isNotBlank(vo.getNotice())) {
                r.setNotice(vo.getNotice());
            }
            if(null != vo.getCreateTime()) {
                r.setCreateTime(DateUtil.parseLocalDateWithAlias(vo.getCreateTime()));
            }
            if(null != vo.getUpdateTime()) {
                r.setUpdateTime(DateUtil.parseLocalDateWithAlias(vo.getUpdateTime()));
            }
            if(StringUtils.isNotBlank(vo.getFileRequired())){
                r.setFileRequired(vo.getFileRequired());
            }
            r.setCompanyId(vo.getCompanyId());
            return r;
        }
        return null;
    }
}
