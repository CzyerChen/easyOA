package easyoa.leavemanager.web.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wuwenze.poi.ExcelKit;
import com.wuwenze.poi.handler.ExcelReadHandler;
import com.wuwenze.poi.pojo.ExcelErrorField;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.common.domain.dto.UserDTO;
import easyoa.common.domain.vo.UserSearch;
import easyoa.common.exception.BussinessException;
import easyoa.core.domain.dto.ReportRelationshipExcel;
import easyoa.core.domain.po.user.User;
import easyoa.core.domain.po.user.UserConfig;
import easyoa.core.domain.po.user.UserDetail;
import easyoa.core.domain.dto.UserExcel;
import easyoa.core.service.DepartmentService;
import easyoa.core.service.UserService;
import easyoa.leavemanager.annotation.Log;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.service.UserReporterService;
import easyoa.leavemanager.utils.ExcelUtils;
import easyoa.leavemanager.utils.ShiroMD5Util;
import easyoa.leavemanager.web.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-27 - 10:51
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "用户操作相关接口", description = "用户操作相关接口")
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserReporterService userReporterService;
    @Autowired
    private CacheService cacheService;
    @Resource
    private ResourceLoader resourceLoader;

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "注册")
    @PostMapping("/regist")
    public ApiResponse regist(@NotBlank(message = "{required}") String username,
                              @NotBlank(message = "{required}") String password) {
        Boolean success = userService.registUser(username, password, null, null, null, null, null);
        if (success) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(500L, "注册存在异常");
        }
    }

    /**
     * 密码重置
     *
     * @param username
     * @param password
     * @param code
     * @return
     */
    @ApiOperation(value = "密码重置")
    @PostMapping("/reset")
    public ApiResponse reset(@NotBlank(message = "{required}") String username,
                             @NotBlank(message = "{required}") String password,
                             @NotBlank(message = "{required}") String code) {
        RMapCache<String, String> codeMap = cacheService.getMailVertifyCodeMap();
        boolean checked = false;
        if (codeMap.containsKey(username)) {
            String verifyCode = codeMap.get(username);
            if (StringUtils.equals(verifyCode, code)) {
                checked = true;
            }
        }
        if (checked) {
            try {
                User user = userService.getUser(username);
                if (user != null) {
                    user.setPassword(ShiroMD5Util.encrypt(username, password));
                    userService.saveUser(user);
                    return ApiResponse.success(true);
                }
            } catch (Exception e) {
                log.error("重置密码遇到异常", e);
                return ApiResponse.internalError();
            }
        }
        return ApiResponse.success(false);
    }

    /**
     * 注册，检查名称重复
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "注册，检查名称重复")
    @GetMapping("/check/name/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username) {
        return userService.getUser(username) == null;
    }

    /**
     * 注册，检查工号重复
     *
     * @param userCode
     * @return
     */
    @ApiOperation(value = "注册，检查工号重复")
    @GetMapping("/check/code/{userCode}")
    public boolean checkUserCode(@NotBlank(message = "{required}") @PathVariable String userCode) {
        return userService.getUserByCode(userCode) == null;
    }


    /**
     * 获取用户分页结果
     * sortField: undefined
     * sortOrder: undefined
     * username: max
     * deptId: 20
     * createTimeFrom: 2019-07-02
     * createTimeTo: 2019-08-03
     * pageSize: 10
     * pageNum: 1
     *
     * @param entry
     * @param userSearch
     * @return
     */
    @ApiOperation(value = "获取用户分页结果", notes = "管理人员专用")
    @GetMapping
    @RequiresPermissions("user:view")
    public Map<String, Object> userList(PageRequestEntry entry, UserSearch userSearch) {
        Integer companyPermission = getCompanyPermission();
        if (Objects.nonNull(companyPermission)) {
            userSearch.setCompanyId(companyPermission);
        }
        Pageable pageable = getPageRequest(entry);
        Page<UserDTO> pageUsers = userService.getPageUsersWithSearchParam(pageable, userSearch);
        return getMap(pageUsers);
    }

    /**
     * 获取所有用户
     *
     * @return
     */
    @ApiOperation(value = "获取所有用户", notes = "管理人员专用")
    @GetMapping("all")
    @RequiresPermissions("user:view")
    public Map<String, Object> allUser() {
        List<UserDTO> pageUsers = userService.getUsers();
        return getMap(pageUsers);
    }

    @ApiOperation(value = "获取本公司所有用户", notes = "管理人员专用")
    @GetMapping("cpy")
    @RequiresPermissions("user:view")
    public Map<String, Object> allCompanyUsers() {
        Integer companyPermission = getCompanyPermission();
        List<UserDTO> pageUsers = userService.getUsers(companyPermission);
        return getMap(pageUsers);
    }

    /**
     * 上传用户详情
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "上传用户详情", notes = "管理人员专用")
    @PostMapping("/upload/detail")
//    @RequiresPermissions("user:upload")
    public ApiResponse uploadUserDetail(@RequestParam("excel-user") MultipartFile file) throws IOException {
        final List<UserExcel> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(UserExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<UserExcel>() {
            @Override
            public void onSuccess(int sheet, int row, UserExcel userExcel) {
                // 数据校验成功时，加入集合
                data.add(userExcel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // 数据校验失败时，记录到 error集合
                error.add(ImmutableMap.of("row", row, "errorFields", errorFields));
            }
        });

        List<UserExcel> list = data.stream()
                .filter(u -> StringUtils.isNotBlank(u.getUserCode())
                        && StringUtils.isNotBlank(u.getCenter())
                        && StringUtils.isNotBlank(u.getDeptName()))
                .collect(Collectors.toList());
        if (list != null && list.size() != 0) {
            List<String> results = userService.checkUserExcelContent(list);
            if (CollectionUtils.isEmpty(results)) {
                //user_dept映射
                userService.saveUserDepartment(list);
                //user_reporter
                userService.saveUserReporter(list);
                //新建userdetail
                userService.saveUserDetail(list);
                return ApiResponse.success(null);
            } else {
                //反馈异常信息
                return ApiResponse.success(String.join(";", results));
            }

        }
        return ApiResponse.success();
    }

    /**
     * 下载员工数据模板
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "下载员工数据模板", notes = "管理人员专用")
    @PostMapping("/template")
    @RequiresPermissions("user:view")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:员工模板.xlsx");
            // 设置excel的文件名称
            String excelName = "员工信息模板.xlsx";
            // 重置响应对象
            response.reset();
            // 当前日期，用于导出文件名称
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
            // 指定下载的文件名--设置响应头
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");//禁止缓存

            servletOutputStream = response.getOutputStream();
            inputStream = resource.getInputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();

            // 写出数据输出流到页面
//            BufferedOutputStream bufferedOutPut = null;
//            try {
//                OutputStream output = response.getOutputStream();
//                bufferedOutPut = new BufferedOutputStream(output);
//                reader.write(bufferedOutPut);
//                bufferedOutPut.flush();
//                bufferedOutPut.close();
//                output.close();
//            } catch (IOException e) {
//                log.error("员工信息模板下载异常", e);
//            } finally {
//                workbook.close();
//                if (bufferedOutPut != null) {
//                    bufferedOutPut.close();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                    servletOutputStream = null;
                }
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                // 召唤jvm的垃圾回收器
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // 获取workbook对象
//        Workbook workbook = ExcelUtils.exportUserSheetByTemplate();
            // 判断数据
//        if (workbook != null) {
//
//            // 设置excel的文件名称
//            String excelName = "员工信息模板";
//            // 重置响应对象
//            response.reset();
//            // 当前日期，用于导出文件名称
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
//            // 指定下载的文件名--设置响应头
//            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
//            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//            response.setHeader("Pragma", "No-cache");
//            response.setHeader("Cache-Control", "no-cache");
//            response.setHeader("Expires", "0");//禁止缓存
//
//            // 写出数据输出流到页面
//            BufferedOutputStream bufferedOutPut = null;
//            try {
//                OutputStream output = response.getOutputStream();
//                bufferedOutPut = new BufferedOutputStream(output);
//                workbook.write(bufferedOutPut);
//                bufferedOutPut.flush();
//                bufferedOutPut.close();
//                output.close();
//            } catch (IOException e) {
//                log.error("员工信息模板下载异常", e);
//            } finally {
//                workbook.close();
//                if (bufferedOutPut != null) {
//                    bufferedOutPut.close();
//                }
//            }
//        }
        }
    }

    /**
     * 下载员工数据模板
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "下载员工汇报关系模板", notes = "管理人员专用")
    @PostMapping("/reporter/template")
    @RequiresPermissions("user:view")
    public void downloadReporterTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取workbook对象
        Workbook workbook = ExcelUtils.exportUserReporterByTemplate();
        // 判断数据
        if (workbook != null) {

            // 设置excel的文件名称
            String excelName = "员工汇报关系模板";
            // 重置响应对象
            response.reset();
            // 当前日期，用于导出文件名称
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
            // 指定下载的文件名--设置响应头
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");//禁止缓存

            // 写出数据输出流到页面
            BufferedOutputStream bufferedOutPut = null;
            try {
                OutputStream output = response.getOutputStream();
                bufferedOutPut = new BufferedOutputStream(output);
                workbook.write(bufferedOutPut);
                bufferedOutPut.flush();
                bufferedOutPut.close();
                output.close();
            } catch (IOException e) {
                log.error("员工汇报关系模板下载异常", e);
            } finally {
                workbook.close();
                if (bufferedOutPut != null) {
                    bufferedOutPut.close();
                }
            }
        }
    }

    /**
     * 更新用户个人信息
     * 前端主要提交信息有：邮箱 手机 性别 昵称 部门
     *
     * @param user
     */
    @ApiOperation(value = "更新用户个人信息")
    @PutMapping("/profile")
    public void updateProfile(@Valid UserDTO user) {
        //更新user和userDetail的信息
        User oldUser = userService.getUser(user.getUserId());

        //保存user-dept映射
        departmentService.saveUserDepartment(oldUser.getUserName(), user.getDeptId());
        //保存用户表
        User newUser = null;
        if (oldUser != null) {
            if (StringUtils.isNotBlank(user.getNickName())) {
                oldUser.setNickName(user.getNickName());
            }
            if (null != user.getDeptId()) {
                oldUser.setDeptId(user.getDeptId());
            }
            if (null != user.getSex()) {
                oldUser.setSex(user.getSex());
            }

            newUser = userService.saveUser(oldUser);
        } else {
            log.error("用户部门/昵称保存失败，{}", user.getUserId());
        }

        //保存邮件和电话
        UserDetail userDetail = userService.findUserDetailByUserId(user.getUserId());
        UserDetail newUserDetail = null;
        if (userDetail != null) {
            if (StringUtils.isNotBlank(user.getEmail())) {
                userDetail.setEmail(user.getEmail());
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                userDetail.setPhone(user.getPhone());
            }
            newUserDetail = userService.saveUserDetail(userDetail);
        }

        //刷新缓存
        userService.refreshCacheForUser(newUser, newUserDetail);
    }

    /**
     * 获取用户信息
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/{username}")
    public UserDTO getUserInfo(@NotBlank(message = "{required}") @PathVariable String username) {
        //获取最新的user信息，刷新前端数据
        return userService.getCurrentUserInfo(username);
    }

    /**
     * 检查密码-是否与原来的一致
     *
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "检查密码")
    @GetMapping("/password/check")
    public boolean checkPassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        String encryptPassword = ShiroMD5Util.encrypt(username, password);
        User user = userService.getUser(username);
        if (user != null) {
            return StringUtils.equals(user.getPassword(), encryptPassword);
        } else {
            return false;
        }
    }

    /**
     * 修改密码
     *
     * @param username
     * @param password
     * @throws BussinessException
     */
    @ApiOperation(value = "修改密码")
    @PutMapping("/password")
    public void updatePassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws BussinessException {
        try {
            userService.updatePassword(username, password);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            throw new BussinessException("修改密码失败");
        }
    }

    /**
     * @param usernames
     * @throws BussinessException
     */
    @ApiOperation(value = "", notes = "")
    @PutMapping("/password/reset")
    @RequiresPermissions("user:reset")
    public void resetPassword(@NotBlank(message = "{required}") String usernames) throws BussinessException {
        try {
            String[] usernameArr = usernames.split(StringPool.COMMA);
            this.userService.resetPassword(usernameArr);
        } catch (Exception e) {
            log.error("重置用户密码失败", e);
            throw new BussinessException("重置用户密码失败");
        }
    }

    /**
     * 更新用户系统配置
     * multiPage: 0
     * theme: light
     * fixedSiderbar: 1
     * fixedHeader: 1
     * layout: side
     * color: rgb(82,196, 26)
     * userId: 7
     */
    @ApiOperation(value = "更新用户系统配置")
    @PutMapping("/userconfig")
    public void updateUserConfig(@Valid UserConfig userConfig) {
        try {
            userService.updateUserConfig(userConfig);
        } catch (Exception e) {
            log.error("修改系统配置失败 {}", e);
            throw new BussinessException("修改系统配置失败");
        }
    }

    /**
     * 修改用户头像
     *
     * @param username
     * @param avatar
     * @throws BussinessException
     */
    @ApiOperation(value = "修改用户头像")
    @PutMapping("/avatar")
    public void updateAvatar(@NotBlank(message = "{reuqired}") String username,
                             @NotBlank(message = "required") String avatar) throws BussinessException {
        try {
            userService.updateAvatar(username, avatar);
        } catch (Exception e) {
            log.error("用户头像修改失败{}", e);
            throw new BussinessException("用户头像修改失败");
        }
    }

    /**
     * 新增用户
     * userName: dddddd@qq.com
     * * userCode: OA00006
     * * roleId: 2
     * * deptId: 20
     * * status: 1
     * * sex: M
     *
     * @param user
     */
    @ApiOperation(value = "新增用户", notes = "管理人员专用")
    @Log("新增用户")
    @PostMapping
    @RequiresPermissions("user:add")
    public ApiResponse addUser(UserDTO user) {
        try {
            if (Objects.isNull(user.getCompanyId())) {
                Integer companyPermission = getCompanyPermission();
                if (Objects.nonNull(companyPermission)) {
                    user.setCompanyId(companyPermission);
                }
            }
            userService.registUser(user.getUserName(), null, user.getRoleId(), user.getDeptId(), user.getCompanyId(), user.getSex(), user.getStatus());
        } catch (BussinessException e) {
            return ApiResponse.success(e.getMessage());
        }catch (Exception e){
            return ApiResponse.success("新增失败，请通知管理员");
        }
        return ApiResponse.success();
    }

    /**
     * 修改用户
     *
     * @param user
     */
    @ApiOperation(value = "修改用户", notes = "管理人员专用")
    @Log("修改用户")
    @PutMapping
    @RequiresPermissions("user:update")
    public void updateUser(UserDTO user) {
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            log.error("用户修改失败{}", e);
            throw new BussinessException("用户修改失败");
        }

    }

    /**
     * 删除用户
     *
     * @param userIds
     */
    @ApiOperation(value = "删除用户", notes = "管理人员专用")
    @Log("删除用户")
    @DeleteMapping("/{userIds}")
    @RequiresPermissions("user:delete")
    public void deleteUser(@NotBlank(message = "{required}") @PathVariable String userIds) {
        String[] ids = userIds.split(StringPool.COMMA);
        userService.removeUser(ids);
    }

    /**
     * 导入用户汇报关系
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "导入用户汇报关系", notes = "管理人员专用")
    @PostMapping("/excel/reporter")
    public ApiResponse importUserReporter(@RequestParam("excel-reporter") MultipartFile file) throws IOException {
        final List<ReportRelationshipExcel> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(ReportRelationshipExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<ReportRelationshipExcel>() {
            @Override
            public void onSuccess(int sheet, int row, ReportRelationshipExcel excel) {
                // 数据校验成功时，加入集合
                data.add(excel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // 数据校验失败时，记录到 error集合
                error.add(ImmutableMap.of("row", row, "errorFields", errorFields));
            }
        });


        if (data != null && data.size() != 0) {
            userReporterService.saveUserReporterInfo(data);
        }
        return ApiResponse.success();
    }


}
