package easyoa.leavemanager.web.controller;

import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.vo.MailVO;
import easyoa.common.utils.CommonUtil;
import easyoa.leavemanager.domain.GlobalEmailAccount;
import easyoa.leavemanager.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-08-05 - 13:07
 **/
@Slf4j
@RestController
@RequestMapping("mail")
@Api(value = "邮箱配置相关接口",description = "邮箱配置相关接口")
public class EmailController extends AbstractController {
    @Autowired
    private EmailService emailService;

    /**
     * 获取邮箱配置分页结果
     *
     * @param entry
     * @return
     */
    @ApiOperation(value ="获取邮箱配置分页结果",notes = "管理人员专用")
    @GetMapping
    public Map<String, Object> getMailPage(PageRequestEntry entry) {
        PageRequest pageRequest = getPageRequest(entry);
        Page<MailVO> pageMails = emailService.findPageMails(pageRequest);

        return getMap(pageMails);
    }

    /**
     * 删除邮箱配置
     *
     * @param ids
     */
    @ApiOperation(value ="删除邮箱配置",notes = "管理人员专用")
    @DeleteMapping("{ids}")
    @RequiresPermissions("mail:delete")
    public void deleteMails(@NotBlank @PathVariable String ids) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(ids.split(",")));
        if (list != null && list.size() != 0) {
            List<Integer> mails = list.stream().map(Integer::valueOf).distinct().collect(Collectors.toList());
            emailService.deleteGlobalMail(mails);
        }

    }

    /**
     * 保存邮箱配置
     *
     * @param account
     */
    @ApiOperation(value ="保存邮箱配置",notes = "管理人员专用")
    @PostMapping
    @RequiresPermissions("mail:add")
    public void saveMailAccount(GlobalEmailAccount account) {
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(account.getCreateTime());
        account.setActive(true);
        account.setDeleted(false);
        account.setPassword(CommonUtil.encryptToken(account.getPassword()));
        emailService.saveGlobalMail(account);
    }


    /**
     * 更新邮箱配置
     *
     * @param mailVO
     */
    @ApiOperation(value ="更新邮箱配置",notes = "管理人员专用")
    @PutMapping
    @RequiresPermissions("mail:edit")
    public void updateMailPassword(MailVO mailVO) {
        GlobalEmailAccount emailAccount = emailService.findById(mailVO.getId());
        if (emailAccount != null) {
            if (StringUtils.isNotBlank(mailVO.getPassword())) {
                emailAccount.setPassword(mailVO.getPassword());
            }
            if (null != mailVO.getActive()) {
                emailAccount.setActive(mailVO.getActive());
            }
            emailAccount.setUpdateTime(LocalDateTime.now());

            emailService.saveGlobalMail(emailAccount);
        }
    }

    /**
     * 检查邮箱是否已被添加
     *
     * @param mail
     * @return
     */
    @ApiOperation(value ="检查邮箱是否已被添加",notes = "管理人员专用")
    @GetMapping("/check/{mail}")
    @RequiresPermissions("mail:add")
    public Boolean checkEmail(@NotBlank @PathVariable String mail) {
        GlobalEmailAccount email = emailService.findAccountByEmail(mail);
        return email == null;
    }
}
