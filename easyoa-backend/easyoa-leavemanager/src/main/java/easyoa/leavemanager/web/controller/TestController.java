package easyoa.leavemanager.web.controller;

import easyoa.leavemanager.utils.ExcelUtils;
import easyoa.leavemanager.domain.biz.PieEntry;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by claire on 2019-07-18 - 17:39
 **/
@RestController
public class TestController {

    @RequestMapping("/pie")
    public List<PieEntry> getPisData() {
        ArrayList<PieEntry> list = new ArrayList<>();

        PieEntry pieEntry = new PieEntry("已完成", 1);
        PieEntry pieEntry1 = new PieEntry("未完成", 2);
        list.add(pieEntry);
        list.add(pieEntry1);

        return list;
    }

    @PostMapping("/export")
    public String export(HttpServletRequest request,HttpServletResponse response) throws IOException {
        // 获取workbook对象
        Workbook workbook = ExcelUtils.exportUserSheetByTemplate();
        // 判断数据
        if (workbook == null) {
            return "null";
        }
        // 设置excel的文件名称
        String excelName = "测试excel";
        // 重置响应对象
        response.reset();
        // 当前日期，用于导出文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateStr = "[" + excelName + "-" + sdf.format(new Date()) + "]";
        // 指定下载的文件名--设置响应头
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Content-Disposition", "attachment;filename=" + dateStr + ".xls");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        /*response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", String.format("attachment; filename=%s", filename));*/
        // 写出数据输出流到页面
        try {
            OutputStream output = response.getOutputStream();//new FileOutputStream(new File("/Users/demo/files/work/休假/db数据/test.xlsx"));//response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
