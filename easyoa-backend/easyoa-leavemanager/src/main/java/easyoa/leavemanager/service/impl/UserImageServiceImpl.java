package easyoa.leavemanager.service.impl;

import easyoa.core.domain.dto.ImageExcel;
import easyoa.core.domain.po.user.UserImage;
import easyoa.core.repository.UserImageRepository;
import easyoa.core.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-01 - 10:05
 **/
@Service(value = "userImageService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class UserImageServiceImpl implements UserImageService {
    @Autowired
    private UserImageRepository userImageRepository;

    @Override
    public String findUserImageById(Long id) {
        Optional<UserImage> image = userImageRepository.findById(id);
        return image.map(UserImage::getName).orElse(null);
    }

    @Override
    public void saveUserImage(UserImage userImage) {
        userImageRepository.save(userImage);
    }

    @Override
    public void saveImageExcel(List<ImageExcel> list) {
        List<UserImage> images = list.stream().map(imageExcel -> {
            UserImage userImage = new UserImage();
            userImage.setName(imageExcel.getImageName());
            return userImage;
        }).distinct().collect(Collectors.toList());

        if(images.size() > 0){
           userImageRepository.saveAll(images);
        }
    }

    @Override
    public Long findUserImageByName(String name) {
        UserImage userImage = userImageRepository.findByName(name);
        if(userImage != null){
            return userImage.getId();
        }
        return null;
    }
}
