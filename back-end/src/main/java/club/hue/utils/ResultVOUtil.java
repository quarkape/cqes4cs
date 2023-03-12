package club.hue.utils;

import club.hue.vo.ResultVO;

// 封装返回数据

public class ResultVOUtil {

    public static ResultVO successSimple() {
        ResultVO resultVO = new ResultVO();
        resultVO.setData(null);
        resultVO.setCode(0);
        resultVO.setMessage("success");
        return resultVO;
    }

    public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMessage("success");
        return resultVO;
    }

    public static ResultVO error(Integer code, String message) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMessage(message);
        resultVO.setData(null);
        return resultVO;
    }
}