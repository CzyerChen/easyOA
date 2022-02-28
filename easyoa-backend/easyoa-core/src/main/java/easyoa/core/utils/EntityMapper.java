package easyoa.core.utils;

import easyoa.core.domain.po.GlobalVacation;
import easyoa.common.constant.UserConstant;
import easyoa.common.domain.dto.RoleDTO;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.vo.RoleVO;
import easyoa.common.domain.vo.VacationVO;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.Role;
import easyoa.core.domain.po.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-24 - 11:09
 **/
public class EntityMapper {

    public static UserDTO userRole2DTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        List<RoleDTO> roles = new ArrayList<>();
        if(user.getRoleList()!= null) {
            user.getRoleList().forEach(r -> {
                String roleName = r.getRoleName();
                if (StringUtils.isBlank(userDTO.getRoleName())) {
                    userDTO.setRoleName(roleName);
                }

                if (StringUtils.isNotBlank(roleName)) {
                    RoleDTO dto = RoleDTO.builder().roleId(r.getRoleId()).roleName(roleName).build();
                    roles.add(dto);
                }
            });
        }

        String userName = user.getUserName();
        String nickName = user.getNickName();
        char sex = (user.getSex() == UserConstant.USER_SEX_FEMALE ? UserConstant.USER_SEX_FEMALE :
                (user.getSex() == UserConstant.USER_SEX_MALE ? UserConstant.USER_SEX_MALE : UserConstant.USER_SEX_UNKNOW));
        String userCode = user.getUserCode();


        userDTO.setUserId(user.getUserId());
        userDTO.setDeptId(user.getDeptId());
        userDTO.setAvatar(user.getAvatar());

        userDTO.setStatus(user.getStatus());
        userDTO.setSex(sex);
        userDTO.setRoles(roles);
        if(user.getCreateTime() != null){
            userDTO.setCreateTime(DateUtil.format(user.getCreateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
        }
        if (StringUtils.isNotBlank(userName)) {
            userDTO.setUserName(userName);
        }
        if (StringUtils.isNotBlank(nickName)) {
            userDTO.setNickName(nickName);
        }
        if (StringUtils.isNotBlank(userCode)) {
            userDTO.setUserCode(userCode);
        }
        return userDTO;
    }


    public static Page<RoleDTO> roleDTOPage(Page<Role> page) {
        List<RoleDTO> list = new ArrayList<>();
        if (page.getContent() != null && page.getContent().size() != 0) {
            list = page.getContent().stream().map(r ->
                    RoleDTO.builder().roleId(r.getRoleId()).roleName(r.getRoleName())
                            .createTime(DateUtil.format(r.getCreateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()))
                            .updateTime(DateUtil.format(r.getUpdateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()))
                            .description(r.getDescription())
                            .build()).distinct().collect(Collectors.toList());
        }
        return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
    }

    public static List<RoleDTO> roleDTOList(List<Role> list) {
        if (list != null && list.size() != 0) {
            return list.stream().map(r ->
                    RoleDTO.builder().roleId(r.getRoleId()).roleName(r.getRoleName()).build()).distinct().collect(Collectors.toList());
        }
        return null;
    }

    public static Role vo2Role(RoleVO roleVO) {
        if (roleVO != null) {
            Role role = new Role();
            if (StringUtils.isNotBlank(roleVO.getRoleName())) {
                role.setRoleName(roleVO.getRoleName());
            }
            if (StringUtils.isNotBlank(roleVO.getDescription())) {
                role.setDescription(roleVO.getDescription());
            }
            if (roleVO.getCreateTime() != null) {
                role.setCreateTime(roleVO.getCreateTime());
            }
            if (roleVO.getUpdateTime() != null) {
                role.setUpdateTime(roleVO.getUpdateTime());
            }
            role.setDeleted(false);
            return role;
        }

        return null;
    }

    public static List<VacationVO> vacation2Vos(List<GlobalVacation> list){
        List<VacationVO> vos = new ArrayList<>();

        list.forEach(v ->{
            VacationVO vo = new VacationVO();
            vo.setVacationId(v.getId());
            vo.setFestival(v.getFestival().toString());
            vo.setName(v.getName());
            vo.setDays(v.getDays());
            vo.setDetail(v.getDetail());
            vo.setAdvice(v.getAdvice());
            vo.setDescription(v.getDescription());
            vo.setEndDate(v.getEndDate().toString());
            vo.setStartDate(v.getStartDate().toString());

            vos.add(vo);
        });
        return vos;
    }

    public static GlobalVacation vacationFromVo(VacationVO vacationVO){
        GlobalVacation v = new GlobalVacation();
        v.setId(vacationVO.getVacationId());
        v.setFestival(DateUtil.parseLocalDate(vacationVO.getFestival()));
        v.setName(vacationVO.getName());
        v.setStartDate(DateUtil.parseLocalDate(vacationVO.getStartDate()));
        v.setEndDate(DateUtil.parseLocalDate(vacationVO.getEndDate()));
        v.setDetail(vacationVO.getDetail());
        v.setDescription(vacationVO.getDescription());
        v.setDays(vacationVO.getDays());
        v.setAdvice(vacationVO.getAdvice());
        v.setFinish(true);
        return v;
    }




}
