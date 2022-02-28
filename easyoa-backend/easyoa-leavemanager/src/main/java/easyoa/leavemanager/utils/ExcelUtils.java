package easyoa.leavemanager.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import easyoa.core.domain.dto.UserExcelTemplate;
import easyoa.core.domain.dto.UserReporterExcelTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class ExcelUtils {

    public static <T> List<T> importExcelWithEasyPOI(InputStream ips, Integer titleRows, Integer headerRows, Class<T> clazz) {
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(ips, clazz, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> Workbook exportExcelWithEasyPOI(String titleName, String sheetName, Class<T> clazz, List<T> datas) {
       return ExcelExportUtil.exportExcel(new ExportParams(titleName, sheetName), clazz, new ArrayList<>(datas));
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) throws Exception {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) throws Exception {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws Exception {
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws Exception {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) throws Exception {
        ServletOutputStream outStream=null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(outStream);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }finally {
            workbook.close();
            outStream.close();
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws Exception {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new Exception("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws Exception {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new Exception("excel文件不能为空");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return list;
    }

    public static Workbook exportUserSheetByTemplate() throws IOException {
        // 查询数据,此处省略
        List<UserExcelTemplate> list = new ArrayList<UserExcelTemplate>();
        UserExcelTemplate easyPOIModel1 = new UserExcelTemplate("1","admin001","admin1","admin1","正式员工",
                "demo","研发部","大数据工程师","12345678910","123456@qq.com",
                "女","未婚", "2019/04/01", "2019/04/01","17.32","2019/04/01","10","1","" ) ;
        list.add(easyPOIModel1) ;
        easyPOIModel1 = null ;

        // 设置导出配置
        // 获取导出excel指定模版
        /*URL resource = ExcelUtils.class.getClassLoader().getResource("userDetailSheet2.xlsx");
        TemplateExportParams params = new TemplateExportParams(resource.getPath());*/
        TemplateExportParams params = new TemplateExportParams(convertTemplatePath("userDetailSheet2.xlsx"));
        // 标题开始行
        // params.setHeadingStartRow(0);
        // 标题行数
        // params.setHeadingRows(2);
        // 设置sheetName，若不设置该参数，则使用得原本得sheet名称
        params.setSheetName("员工信息");
        Map<String,Object> map = new HashMap<>() ;
        map.put("list",list) ;
        // 导出excel
        return ExcelExportUtil.exportExcel(params, map);
    }


    public static Workbook exportUserReporterByTemplate() throws IOException {
        // 查询数据,此处省略
        List<UserReporterExcelTemplate> list = new ArrayList<UserReporterExcelTemplate>();
        UserReporterExcelTemplate easyPOIModel1 = new UserReporterExcelTemplate("张三","001","李四(可不填)","002(可不填)","王五(可不填)","003(可不填)") ;
        list.add(easyPOIModel1) ;
        easyPOIModel1 = null ;

        // 设置导出配置
        // 获取导出excel指定模版
       /* URL resource = ExcelUtils.class.getClassLoader().getResource("userReporterSheet2.xlsx");
        TemplateExportParams params = new TemplateExportParams(resource.getPath());*/
        TemplateExportParams params = new TemplateExportParams(convertTemplatePath("userReporterSheet2.xlsx"));
        // 标题开始行
        // params.setHeadingStartRow(0);
        // 标题行数
        // params.setHeadingRows(2);
        // 设置sheetName，若不设置该参数，则使用得原本得sheet名称
        params.setSheetName("员工汇报关系");
        Map<String,Object> map = new HashMap<>() ;
        map.put("list",list) ;
        // 导出excel
        return ExcelExportUtil.exportExcel(params, map);
    }


    public static String convertTemplatePath(String path) {
         //如果是windows 则直接返回
         if (System.getProperties().getProperty("os.name").contains("Windows")) {
         return path;
         }

        Resource resource = new ClassPathResource(path);
        FileOutputStream fileOutputStream = null;
        // 将模版文件写入到 tomcat临时目录
        String folder = System.getProperty("catalina.home");
        File tempFile = new File(folder + File.separator + path);
        // System.out.println("文件路径：" + tempFile.getPath());
        // 文件存在时 不再写入
        if (tempFile.exists()) {
            return tempFile.getPath();
        }
        File parentFile = tempFile.getParentFile();
        // 判断父文件夹是否存在
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(resource.getInputStream());
            fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[10240];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tempFile.getPath();
    }


}
