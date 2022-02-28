package easyoa.leavemanager.web.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wuwenze.poi.ExcelKit;
import com.wuwenze.poi.handler.ExcelReadHandler;
import com.wuwenze.poi.pojo.ExcelErrorField;
import easyoa.leavemanager.web.AbstractController;
import easyoa.common.domain.ApiResponse;
import easyoa.core.domain.dto.ImageExcel;
import easyoa.core.service.UserImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-07-02 - 10:38
 **/
@Slf4j
@RestController
@RequestMapping("/image")
@Api(value="头像相关接口",description = "头像相关接口")
public class ImageController extends AbstractController {
    @Autowired
    private UserImageService userImageService;

    /**
     * 上传头像图片列表
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value ="上传头像列表")
    @PostMapping("/upload")
    public ApiResponse uploadUserImage(@RequestParam("excel-image")MultipartFile file) throws IOException {
        final List<ImageExcel> data = Lists.newArrayList();
        //异常的数据，需要提示或者处理
        final List<Map<String, Object>> error = Lists.newArrayList();
        ExcelKit.$Import(ImageExcel.class).readXlsx(file.getInputStream(), new ExcelReadHandler<ImageExcel>() {
            @Override
            public void onSuccess(int i, int i1, ImageExcel imageExcel) {
                data.add(imageExcel);
            }
            @Override
            public void onError(int sheet, int row, List<ExcelErrorField> list) {
                // 数据校验失败时，记录到 error集合
                error.add(ImmutableMap.of("row", row, "errorFields", list));
            }
        });//ExcelUtils.importExcelWithEasyPOI(file.getInputStream(), 0, 1, DeptExcel.class);

        if(data != null && data.size()>0){
            userImageService.saveImageExcel(data);
        }else{
            log.error("图片导入失败,excel为空或者解析失败，请检查excel格式");
        }
        return null;
    }
}
