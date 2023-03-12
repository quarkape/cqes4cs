package club.hue.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;

// base64加密和解密，主要用于登录

public class Base64Dec {
    public String[] getBase64Res(String str) throws UnsupportedEncodingException {
        Decoder decoder = Base64.getMimeDecoder();
        byte[] bs_1 = decoder.decode(str);
        String res = new String(bs_1, "UTF-8");
        String resAll[] = res.split(";");
        int paramsNum = resAll.length;
        String[] result = new String[paramsNum];
        for (int i = 0; i < paramsNum; i++) {
            result[i] = resAll[i].split(":")[1];
        }
        return result;
    }
}
