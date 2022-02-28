package easyoa.core.service;

import easyoa.core.domain.dto.ImageExcel;
import easyoa.core.domain.po.user.UserImage;

import java.util.List;

/**
 * Created by claire on 2019-07-01 - 10:04
 **/
public interface UserImageService {
    String findUserImageById(Long id);

    void saveUserImage(UserImage userImage);

    void saveImageExcel(List<ImageExcel> list);

    Long findUserImageByName(String name);

}
