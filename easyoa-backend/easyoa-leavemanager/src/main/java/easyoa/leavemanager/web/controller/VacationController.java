package easyoa.leavemanager.web.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wuwenze.poi.ExcelKit;
import com.wuwenze.poi.handler.ExcelReadHandler;
import com.wuwenze.poi.pojo.ExcelErrorField;
import easyoa.common.domain.vo.UserVacationVO;
import easyoa.common.domain.vo.VacationCalSearch;
import easyoa.common.domain.vo.VacationCalendar;
import easyoa.common.domain.vo.VacationVO;
import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.common.domain.PageRequestEntry;
import easyoa.core.domain.dto.VacationExcel;
import easyoa.core.domain.po.GlobalVacation;
import easyoa.core.service.VacationService;
import easyoa.core.utils.EntityMapper;
import easyoa.leavemanager.annotation.Log;
import easyoa.leavemanager.domain.dto.UserVacationCalDTO;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.leavemanager.domain.vo.VacationCalExcel;
import easyoa.leavemanager.runner.api.RuleServer;
import easyoa.leavemanager.service.UserVacationCalService;
import easyoa.leavemanager.service.UserVacationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by claire on 2019-07-09 - 11:48
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/vacation")
@Api(value = "????????????????????????",description = "????????????????????????")
public class VacationController extends AbstractController {

    @Autowired
    private VacationService vacationService;
    @Autowired
    private UserVacationService userVacationService;
    @Autowired
    private RuleServer ruleServer;
    @Autowired
    private UserVacationCalService userVacationCalService;

    /**
     * ??????????????????
     * @return
     */
    @ApiOperation(value = "??????????????????",notes = "??????????????????")
    @GetMapping
    @RequiresPermissions("vacation:view")
    public ApiResponse getVacations() {
        List<GlobalVacation> list = vacationService.findAll();
        if (list != null) {
            List<VacationVO> vacationTables = EntityMapper.vacation2Vos(list);
            return ApiResponse.success(vacationTables);
        }
        return null;
    }

    /**
     * ??????????????????????????????
     * @param entry
     * @return
     */
    @ApiOperation(value = "??????????????????????????????",notes = "??????????????????")
    @GetMapping("page")
    @RequiresPermissions("vacation:view")
    public Map<String, Object> getPageVacations(PageRequestEntry entry) {
        PageRequest pageRequest = getPageRequest(entry);
        Page<GlobalVacation> page = vacationService.findAll(pageRequest);
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            return getMap(page);
        }
        return null;
    }

    /**
     * ????????????
     * @param vacation
     * @param vacationId
     */
    @ApiOperation(value = "????????????",notes = "??????????????????")
    @Log("????????????")
    @PutMapping("{vacationId}")
    @RequiresPermissions("vacation:edit")
    public void updateVacation(@Valid VacationVO vacation, @PathVariable Integer vacationId) {
        if (vacation != null) {
            GlobalVacation globalVacation = EntityMapper.vacationFromVo(vacation);
            vacationService.saveVacation(globalVacation);
        }
    }

    /**
     *  ????????????????????????
     * @param vacationId
     * @return
     */
    @ApiOperation(value = "????????????????????????",notes = "??????????????????")
    @GetMapping("/{vacationId}")
    public ApiResponse getVacationById(@NotBlank(message = "{required}") @PathVariable Integer vacationId) {
        VacationVO vacation = vacationService.findVacationById(vacationId);
        if (vacation != null) {
            return ApiResponse.success(vacation);
        }
        return null;
    }

    /**
     *  ?????????????????????????????????
     * @param year
     * @param month
     * @param day
     * @return
     */
    @ApiOperation(value = "?????????????????????????????????",notes = "??????????????????")
    @GetMapping("/calendar/{year}/{month}/{day}")
    public ApiResponse getVacationCalendar(@PathVariable String year, @PathVariable String month, @PathVariable String day) {
        VacationCalendar vacationCalendar = vacationService.findCalendarInfo(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
        return ApiResponse.success(vacationCalendar);
    }

    /**
     * ??????????????????????????????
     * @param userId
     * @return
     */
    @ApiOperation(value = "??????????????????????????????" )
    @GetMapping("user/{userId}")
    public ApiResponse getUserVacation(@NotBlank @PathVariable String userId,Integer year) {
        if(Objects.isNull(year)){
            year = LocalDate.now().getYear();
        }
        UserVacation vacation = userVacationService.findByUserId(Long.valueOf(userId),year);
        if (vacation != null) {
            UserVacationVO userVacationVO = userVacationService.vacation2VO(vacation,year);
            if (userVacationVO != null) {
                return ApiResponse.success(userVacationVO);
            }
        }
        return null;
    }

    /**
     * ??????????????????????????????
     * @param vacationCalSearch
     * @param entry
     * @return
     */
    @ApiOperation(value = "??????????????????????????????",notes = "??????????????????")
    @GetMapping("cal")
    @RequiresPermissions("lmsdata:view")
    public Map<String, Object> getPageVacationCal(VacationCalSearch vacationCalSearch, PageRequestEntry entry) {
        Page<UserVacationCalDTO> page = null;
        Integer companyPermission = getCompanyPermission();
        if(Objects.nonNull(companyPermission)){
            vacationCalSearch.setCompanyId(companyPermission);
        }
        //????????????????????????????????????????????????????????????????????????
        PageRequest pageRequest = new PageRequest(0,10000);
        if(StringUtils.isBlank(vacationCalSearch.getUserCode())){
            page = userVacationCalService.findPageUserVacationCal(vacationCalSearch, pageRequest);
        }else{//???????????????????????????????????????
            Boolean result = ruleServer.updateUserVacationCal(vacationCalSearch);
            if(result){
                page = userVacationCalService.findPageUserVacationCal(vacationCalSearch, pageRequest);
            }
        }
        log.info("????????????????????????");
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            log.info("???????????????????????????{}",page.getContent().size());
            return getMap(page);
        }
        log.info("???????????????????????????0");
        return getEmptyMap();
    }

    /**
     * ??????????????????????????????
     * @param vacationCalSearch
     * @param entry
     * @return
     */
    @ApiOperation(value = "??????????????????????????????",notes = "??????????????????")
    @PostMapping("cal/excel")
    @RequiresPermissions("check:export")
    public void exportPageVacationCal(VacationCalSearch vacationCalSearch, PageRequestEntry entry,HttpServletRequest request,HttpServletResponse response) {
        Page<UserVacationCalDTO> page = null;
        PageRequest pageRequest =new PageRequest(0,10000);
        //????????????????????????????????????????????????????????????????????????
        if(StringUtils.isBlank(vacationCalSearch.getUserCode())){
            page = userVacationCalService.findPageUserVacationCal(vacationCalSearch, pageRequest);
        }else{//???????????????????????????????????????
            Boolean result = ruleServer.updateUserVacationCal(vacationCalSearch);
            if(result){
                page = userVacationCalService.findPageUserVacationCal(vacationCalSearch, pageRequest);
            }
        }
        if (page != null && page.getContent() != null && page.getContent().size() != 0) {
            Workbook workbook = null;
            try{
                List<UserVacationCalDTO> content = page.getContent();
                /*response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Content-Disposition", "attachment;filename=" + "????????????????????????" + ".xlsx");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Expires", "0");//????????????

                ExcelKit.$Export(UserVacationCal.class, response).downXlsx(content, false);
          */
                List<VacationCalExcel> list = new ArrayList<>();
                content.forEach(u ->{
                    VacationCalExcel e = new VacationCalExcel();
                    e.setAnnual(u.getAnnual());
                    e.setAnnualCal(u.getAnnualCal());
                    e.setAnnualRest(u.getAnnualRest());
                    e.setAnnualShould(u.getAnnualShould());
                    e.setCasual(u.getCasual());
                    e.setFuneral(u.getFuneral());
                    e.setHireDate(u.getHireDate());
                    e.setMarriage(u.getMarriage());
                    e.setMaterPaternity(u.getMaterPaternity());
                    e.setUserCode(u.getUserCode());
                    e.setUserName(u.getUserName());
                    e.setSickNormal(u.getSickNormal());
                    e.setSick(u.getSick());
                    list.add(e);
                });
                ExportParams params = new ExportParams("??????????????????", null, "??????????????????");
                workbook = ExcelExportUtil.exportExcel(params,VacationCalExcel.class, list);
                workbook.write(response.getOutputStream());
                workbook.close();
            }catch (Exception e){
               log.error("??????????????????");
            }finally {
                workbook = null;
            }
        }
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @PostMapping("/excel")
    public void importHolidayList(@RequestParam("excel-vacation")MultipartFile file) throws IOException {
        final List<VacationExcel> data = Lists.newArrayList();
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(VacationExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<VacationExcel>() {
            @Override
            public void onSuccess(int sheet, int row, VacationExcel excel) {
                // ????????????????????????????????????
                data.add(excel);
            }

            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> errorFields) {
                // ????????????????????????????????? error??????
                error.add(ImmutableMap.of("row", row, "errorFields", errorFields));
            }
        });


        if (CollectionUtils.isNotEmpty(data)) {
            vacationService.processGlobalVacation(data);
        }
    }

}
