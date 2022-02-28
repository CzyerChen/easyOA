package easyoa.common.utils;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.Security;
import java.util.Random;

/**
 * 加密、解密类
 * Created by claire on 2019-06-21 - 16:19
 **/
public class EncryptUtil {
    //设置默认密匙
    private static String strDefaultKey = "defaultKey";
    //加密
    private Cipher encryptCipher = null;
    //解密
    private Cipher decryptCipher = null;

    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";


    private static String byteArr2HexStr(byte[] arrB) {
        int iLen = arrB.length;
        StringBuilder sb = new StringBuilder(iLen * 2);
        for (byte anArrB : arrB) {
            int intTmp = anArrB;
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    private static byte[] hexStr2ByteArr(String strIn) {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    public EncryptUtil() throws Exception {
        this(strDefaultKey);
    }

    EncryptUtil(String strKey) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key = getKey(strKey.getBytes());

        encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    private byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    String encrypt(String strIn) throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes()));
    }

    private byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    String decrypt(String strIn) {
        try {
            return new String(decrypt(hexStr2ByteArr(strIn)));
        } catch (Exception e) {
            return "";
        }
    }

    private Key getKey(byte[] arrBTmp) {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return new javax.crypto.spec.SecretKeySpec(arrB, "DES");
    }


    public static String randomMailVertifyCode(){
        char[] chars = new char[6];
        Random rnd = new Random();

        chars[nextIndex(chars, rnd)] = nextSpecialChar(rnd);
        chars[nextIndex(chars, rnd)] = nextUpperlLetter(rnd);
        chars[nextIndex(chars, rnd)] = nextLowerLetter(rnd);
        chars[nextIndex(chars, rnd)] = nextNumLetter(rnd);

        for (int i = 0; i < 6; i++) {
            if (chars[i] == 0) {
                chars[i] = nextChar(rnd);
            }
        }
       return new String(chars);
    }



    private static char nextChar(Random rnd) {
        switch (rnd.nextInt(4)) {
            case 0:
                return (char) ('a' + rnd.nextInt(26));
            case 1:
                return (char) ('A' + rnd.nextInt(26));
            case 2:
                return (char) ('0' + rnd.nextInt(10));
            default:
                return SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length()));
        }
    }

    private static int nextIndex(char[] chars, Random rnd) {
        int index = rnd.nextInt(chars.length);
        while (chars[index] != 0) {
            index = rnd.nextInt(chars.length);
        }
        return index;
    }

    private static char nextSpecialChar(Random rnd) {
        return SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length()));
    }

    private static char nextUpperlLetter(Random rnd) {
        return (char) ('A' + rnd.nextInt(26));
    }

    private static char nextLowerLetter(Random rnd) {
        return (char) ('a' + rnd.nextInt(26));
    }

    private static char nextNumLetter(Random rnd) {
        return (char) ('0' + rnd.nextInt(10));
    }

}
