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
@Api(value = "????????????????????????", description = "????????????????????????")
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
     * ??????
     *
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "??????")
    @PostMapping("/regist")
    public ApiResponse regist(@NotBlank(message = "{required}") String username,
                              @NotBlank(message = "{required}") String password) {
        Boolean success = userService.registUser(username, password, null, null, null, null, null);
        if (success) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(500L, "??????????????????");
        }
    }

    /**
     * ????????????
     *
     * @param username
     * @param password
     * @param code
     * @return
     */
    @ApiOperation(value = "????????????")
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
                log.error("????????????????????????", e);
                return ApiResponse.internalError();
            }
        }
        return ApiResponse.success(false);
    }

    /**
     * ???????????????????????????
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "???????????????????????????")
    @GetMapping("/check/name/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username) {
        return userService.getUser(username) == null;
    }

    /**
     * ???????????????????????????
     *
     * @param userCode
     * @return
     */
    @ApiOperation(value = "???????????????????????????")
    @GetMapping("/check/code/{userCode}")
    public boolean checkUserCode(@NotBlank(message = "{required}") @PathVariable String userCode) {
        return userService.getUserByCode(userCode) == null;
    }


    /**
     * ????????????????????????
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
    @ApiOperation(value = "????????????????????????", notes = "??????????????????")
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
     * ??????????????????
     *
     * @return
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @GetMapping("all")
    @RequiresPermissions("user:view")
    public Map<String, Object> allUser() {
        List<UserDTO> pageUsers = userService.getUsers();
        return getMap(pageUsers);
    }

    @ApiOperation(value = "???????????????????????????", notes = "??????????????????")
    @GetMapping("cpy")
    @RequiresPermissions("user:view")
    public Map<String, Object> allCompanyUsers() {
        Integer companyPermission = getCompanyPermission();
        List<UserDTO> pageUsers = userService.getUsers(companyPermission);
        return getMap(pageUsers);
    }

    /**
     * ??????????????????
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @PostMapping("/upload/detail")
//    @RequiresPermissions("user:upload")
    public ApiResponse uploadUserDetail(@RequestParam("excel-user") MultipartFile file) throws IOException {
        final List<UserExcel> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(UserExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<UserExcel>() {
            @Override
            public void onSuccess(int sheet, int row, UserExcel userExcel) {
                // ????????????????????????????????????
                data.add(userExcel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // ????????????????????????????????? error??????
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
                //user_dept??????
                userService.saveUserDepartment(list);
                //user_reporter
                userService.saveUserReporter(list);
                //??????userdetail
                userService.saveUserDetail(list);
                return ApiResponse.success(null);
            } else {
                //??????????????????
                return ApiResponse.success(String.join(";", results));
            }

        }
        return ApiResponse.success();
    }

    /**
     * ????????????????????????
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "????????????????????????", notes = "??????????????????")
    @PostMapping("/template")
    @RequiresPermissions("user:view")
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:????????????.xlsx");
            // ??????excel???????????????
            String excelName = "??????????????????.xlsx";
            // ??????????????????
            response.reset();
            // ???????????????????????????????????????
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
            // ????????????????????????--???????????????
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");//????????????

            servletOutputStream = response.getOutputStream();
            inputStream = resource.getInputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();

            // ??????????????????????????????
//            BufferedOutputStream bufferedOutPut = null;
//            try {
//                OutputStream output = response.getOutputStream();
//                bufferedOutPut = new BufferedOutputStream(output);
//                reader.write(bufferedOutPut);
//                bufferedOutPut.flush();
//                bufferedOutPut.close();
//                output.close();
//            } catch (IOException e) {
//                log.error("??????????????????????????????", e);
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
                // ??????jvm??????????????????
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // ??????workbook??????
//        Workbook workbook = ExcelUtils.exportUserSheetByTemplate();
            // ????????????
//        if (workbook != null) {
//
//            // ??????excel???????????????
//            String excelName = "??????????????????";
//            // ??????????????????
//            response.reset();
//            // ???????????????????????????????????????
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
//            // ????????????????????????--???????????????
//            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
//            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//            response.setHeader("Pragma", "No-cache");
//            response.setHeader("Cache-Control", "no-cache");
//            response.setHeader("Expires", "0");//????????????
//
//            // ??????????????????????????????
//            BufferedOutputStream bufferedOutPut = null;
//            try {
//                OutputStream output = response.getOutputStream();
//                bufferedOutPut = new BufferedOutputStream(output);
//                workbook.write(bufferedOutPut);
//                bufferedOutPut.flush();
//                bufferedOutPut.close();
//                output.close();
//            } catch (IOException e) {
//                log.error("??????????????????????????????", e);
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
     * ????????????????????????
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????")
    @PostMapping("/reporter/template")
    @RequiresPermissions("user:view")
    public void downloadReporterTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ??????workbook??????
        Workbook workbook = ExcelUtils.exportUserReporterByTemplate();
        // ????????????
        if (workbook != null) {

            // ??????excel???????????????
            String excelName = "????????????????????????";
            // ??????????????????
            response.reset();
            // ???????????????????????????????????????
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
            // ????????????????????????--???????????????
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");//????????????

            // ??????????????????????????????
            BufferedOutputStream bufferedOutPut = null;
            try {
                OutputStream output = response.getOutputStream();
                bufferedOutPut = new BufferedOutputStream(output);
                workbook.write(bufferedOutPut);
                bufferedOutPut.flush();
                bufferedOutPut.close();
                output.close();
            } catch (IOException e) {
                log.error("????????????????????????????????????", e);
            } finally {
                workbook.close();
                if (bufferedOutPut != null) {
                    bufferedOutPut.close();
                }
            }
        }
    }

    /**
     * ????????????????????????
     * ???????????????????????????????????? ?????? ?????? ?????? ??????
     *
     * @param user
     */
    @ApiOperation(value = "????????????????????????")
    @PutMapping("/profile")
    public void updateProfile(@Valid UserDTO user) {
        //??????user???userDetail?????????
        User oldUser = userService.getUser(user.getUserId());

        //??????user-dept??????
        departmentService.saveUserDepartment(oldUser.getUserName(), user.getDeptId());
        //???????????????
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
            log.error("????????????/?????????????????????{}", user.getUserId());
        }

        //?????????????????????
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

        //????????????
        userService.refreshCacheForUser(newUser, newUserDetail);
    }

    /**
     * ??????????????????
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "??????????????????")
    @GetMapping("/{username}")
    public UserDTO getUserInfo(@NotBlank(message = "{required}") @PathVariable String username) {
        //???????????????user???????????????????????????
        return userService.getCurrentUserInfo(username);
    }

    /**
     * ????????????-????????????????????????
     *
     * @param username
     * @param password
     * @return
     */
    @ApiOperation(value = "????????????")
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
     * ????????????
     *
     * @param username
     * @param password
     * @throws BussinessException
     */
    @ApiOperation(value = "????????????")
    @PutMapping("/password")
    public void updatePassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws BussinessException {
        try {
            userService.updatePassword(username, password);
        } catch (Exception e) {
            log.error("??????????????????", e);
            throw new BussinessException("??????????????????");
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
            log.error("????????????????????????", e);
            throw new BussinessException("????????????????????????");
        }
    }

    /**
     * ????????????????????????
     * multiPage: 0
     * theme: light
     * fixedSiderbar: 1
     * fixedHeader: 1
     * layout: side
     * color: rgb(82,196, 26)
     * userId: 7
     */
    @ApiOperation(value = "????????????????????????")
    @PutMapping("/userconfig")
    public void updateUserConfig(@Valid UserConfig userConfig) {
        try {
            userService.updateUserConfig(userConfig);
        } catch (Exception e) {
            log.error("???????????????????????? {}", e);
            throw new BussinessException("????????????????????????");
        }
    }

    /**
     * ??????????????????
     *
     * @param username
     * @param avatar
     * @throws BussinessException
     */
    @ApiOperation(value = "??????????????????")
    @PutMapping("/avatar")
    public void updateAvatar(@NotBlank(message = "{reuqired}") String username,
                             @NotBlank(message = "required") String avatar) throws BussinessException {
        try {
            userService.updateAvatar(username, avatar);
        } catch (Exception e) {
            log.error("????????????????????????{}", e);
            throw new BussinessException("????????????????????????");
        }
    }

    /**
     * ????????????
     * userName: dddddd@qq.com
     * * userCode: OA00006
     * * roleId: 2
     * * deptId: 20
     * * status: 1
     * * sex: M
     *
     * @param user
     */
    @ApiOperation(value = "????????????", notes = "??????????????????")
    @Log("????????????")
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
            return ApiResponse.success("?????????????????????????????????");
        }
        return ApiResponse.success();
    }

    /**
     * ????????????
     *
     * @param user
     */
    @ApiOperation(value = "????????????", notes = "??????????????????")
    @Log("????????????")
    @PutMapping
    @RequiresPermissions("user:update")
    public void updateUser(UserDTO user) {
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            log.error("??????????????????{}", e);
            throw new BussinessException("??????????????????");
        }

    }

    /**
     * ????????????
     *
     * @param userIds
     */
    @ApiOperation(value = "????????????", notes = "??????????????????")
    @Log("????????????")
    @DeleteMapping("/{userIds}")
    @RequiresPermissions("user:delete")
    public void deleteUser(@NotBlank(message = "{required}") @PathVariable String userIds) {
        String[] ids = userIds.split(StringPool.COMMA);
        userService.removeUser(ids);
    }

    /**
     * ????????????????????????
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "????????????????????????", notes = "??????????????????")
    @PostMapping("/excel/reporter")
    public ApiResponse importUserReporter(@RequestParam("excel-reporter") MultipartFile file) throws IOException {
        final List<ReportRelationshipExcel> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(ReportRelationshipExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<ReportRelationshipExcel>() {
            @Override
            public void onSuccess(int sheet, int row, ReportRelationshipExcel excel) {
                // ????????????????????????????????????
                data.add(excel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // ????????????????????????????????? error??????
                error.add(ImmutableMap.of("row", row, "errorFields", errorFields));
            }
        });


        if (data != null && data.size() != 0) {
            userReporterService.saveUserReporterInfo(data);
        }
        return ApiResponse.success();
    }


}
