package easyoa.leavemanager.web.controller;


import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.NoticeVO;
import easyoa.leavemanager.domain.user.UserNotice;
import easyoa.leavemanager.service.UserNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-22 - 18:13
 **/
@RestController
@RequestMapping("notice")
@Api(value = "用户消息相关接口",description = "用户消息相关接口")
public class NoticeController extends AbstractController {
    @Autowired
    private UserNoticeService userNoticeService;

    /**
     *  保存系统公告
     * @param noticeVO
     */
    @ApiOperation(value = "保存系统公告",notes = "管理人员专用")
    @RequiresPermissions("notice:add")
    @PostMapping
    public void saveSystemNotice(NoticeVO noticeVO) {
        userNoticeService.saveSystemNotice(noticeVO);
    }

    /**
     * 更新系统公告
     * @param noticeVO
     */
    @ApiOperation(value = "更新系统公告",notes = "管理人员专用")
    @RequiresPermissions("notice:edit")
    @PutMapping
    public void updateSystemNotice(NoticeVO noticeVO){
        UserNotice notice = userNoticeService.findNoticeById(noticeVO.getNoticeId());
        if(notice!= null){
            if(StringUtils.isNotBlank(noticeVO.getTitle())){
                notice.setTitle(noticeVO.getTitle());
            }
            if(StringUtils.isNotBlank(noticeVO.getMsgContent())){
                notice.setMessage(noticeVO.getMsgContent());
            }
        }
    }

    /***
     *  删除系统公告
     * @param ids
     */
    @ApiOperation(value = "删除系统公告",notes = "管理人员专用")
    @RequiresPermissions("notice:delete")
    @DeleteMapping("system/{ids}")
    public void deleteSystemNotice(@NotBlank @PathVariable String ids){
        ArrayList<String> noticeIds = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (noticeIds != null && noticeIds.size() != 0) {
            userNoticeService.deleteByIds(noticeIds.stream().map(Integer::valueOf).distinct().collect(Collectors.toList()));
        }
    }

    /**
     * 删除个人消息
     * @param ids
     */
    @ApiOperation(value = "删除个人消息")
    @RequiresPermissions("mynotes:delete")
    @DeleteMapping("{ids}")
    public void deletePersonalNotice(@NotBlank @PathVariable String ids) {
        ArrayList<String> noticeIds = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (noticeIds != null && noticeIds.size() != 0) {
            userNoticeService.deleteByIds(noticeIds.stream().map(Integer::valueOf).distinct().collect(Collectors.toList()));
        }
    }

    /**
     * 获取个人消息
     * @param entry
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取个人消息")
    @RequiresPermissions("mynotes:view")
    @GetMapping("personal/{userId}")
    public Map<String, Object> getPersonalNoticePage(PageRequestEntry entry, @PathVariable Long userId) {
        PageRequest pageRequest = getPageRequest(entry);
        Page<UserNotice> notices = userNoticeService.findAllPersonalUserNotice(userId, pageRequest);
        if (notices != null && notices.getContent() != null && notices.getContent().size() != 0) {
            List<NoticeVO> list = userNoticeService.notice2Vo(notices.getContent());
            return getMap(new PageImpl<>(list, notices.getPageable(), notices.getTotalElements()));
        } else {
            return getEmptyMap();
        }
    }

    /**
     * 获取系统消息
     * @param entry
     * @return
     */
    @ApiOperation(value = "获取系统消息")
    @RequiresPermissions("mynotes:view")
    @GetMapping("system")
    public Map<String, Object> getSystemNoticePage(PageRequestEntry entry) {
        PageRequest pageRequest = getPageRequest(entry);
        Page<UserNotice> notices = userNoticeService.findAllSystemNotice(pageRequest);
        if (notices != null && notices.getContent() != null && notices.getContent().size() != 0) {
            List<NoticeVO> list = userNoticeService.notice2Vo(notices.getContent());
            return getMap(new PageImpl<>(list, notices.getPageable(), notices.getTotalElements()));
        } else {
            return getEmptyMap();
        }
    }

    /**
     * 获取用户消息
     * @param userId
     * @param entry
     * @return
     */
    @ApiOperation(value = "获取用户消息")
    @RequiresPermissions("mynotes:view")
    @GetMapping("short/{userId}")
    public ApiResponse getUserNotice(@PathVariable Long userId, PageRequestEntry entry) {
        PageRequest pageRequest = new PageRequest(0, 3, Sort.Direction.DESC, "sendTime");
        Page<UserNotice> notices = userNoticeService.findValidPersonalUserNotice(userId, pageRequest);
        if (notices != null && notices.getContent() != null && notices.getContent().size() != 0) {
            List<NoticeVO> list = userNoticeService.notice2Vo(notices.getContent());
            return ApiResponse.success(getMap(new PageImpl<>(list, notices.getPageable(), notices.getTotalElements())));
        } else {
            return ApiResponse.success(getEmptyMap());
        }
    }

    /**
     * 标识消息已读
     * @param noticeIds
     */
    @ApiOperation(value = "标识消息已读")
    @RequiresPermissions("mynotes:view")
    @PutMapping("check")
    public void checkNotice(@NotBlank @RequestParam String noticeIds) {
        ArrayList<String> ids = new ArrayList<>(Arrays.asList(noticeIds.split(",")));
        if (ids != null && ids.size() != 0) {
            List<UserNotice> notices = userNoticeService.findNoticesByIds(ids.stream().map(Integer::valueOf).distinct().collect(Collectors.toList()));
            if (notices != null && notices.size() != 0) {
                notices.forEach(n -> {
                    if (!n.getChecked()) {
                        n.setChecked(true);
                    }
                });
                userNoticeService.saveUserNotice(notices);
            }
        }
    }

    /**
     * 获取系统消息
     * @return
     */
    @ApiOperation(value = "获取系统消息")
    @RequiresPermissions("mynotes:view")
    @GetMapping("short/sys")
    public ApiResponse getSystemUserNotice() {
        PageRequest pageRequest = new PageRequest(0, 3, Sort.Direction.DESC, "sendTime");
        Page<UserNotice> notice = userNoticeService.findValidSystemNotice(pageRequest);
        if (notice != null && notice.getContent() != null && notice.getContent().size() != 0) {
            List<NoticeVO> vos = userNoticeService.notice2Vo(notice.getContent());
            return ApiResponse.success(getMap(new PageImpl<>(vos, notice.getPageable(), notice.getTotalElements())));
        } else {
            return ApiResponse.success(getEmptyMap());

        }
    }
}
