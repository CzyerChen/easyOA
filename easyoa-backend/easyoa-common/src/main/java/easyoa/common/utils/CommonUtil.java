package easyoa.common.utils;


import easyoa.common.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用帮助类
 * Created by claire on 2019-06-21 - 16:20
 **/
@Slf4j
public class CommonUtil {

    /**
     * token 加密
     *
     * @param token token
     * @return 加密后的 token
     */
    public static String encryptToken(String token) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(AppConstant.TOKEN_KEY_PREFIX);
            return encryptUtil.encrypt(token);
        } catch (Exception e) {
            log.info("token加密失败：", e);
            return null;
        }
    }

    /**
     * token 解密
     *
     * @param encryptToken 加密后的 token
     * @return 解密后的 token
     */
    public static String decryptToken(String encryptToken) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(AppConstant.TOKEN_KEY_PREFIX);
            return encryptUtil.decrypt(encryptToken);
        } catch (Exception e) {
            log.info("token解密失败：", e);
            return null;
        }
    }





}
