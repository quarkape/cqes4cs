package club.hue.controller;

import club.hue.mapper.PushMapper;
import club.hue.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

// 所有推送相关的内容都放在这里面
// 包括所有的通知等等
@RestController
public class PushController {

    @Autowired
    private PushMapper pushMapper;

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    // 获取所有的系统通知
    @RequestMapping("/getAllNotifys")
    public ResponseEntity getAllNotifys(HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        List<HashMap<String, Object>> notifys = pushMapper.getAllNotifys(userid);
        return ResponseEntity.ok().body(ResultVOUtil.success(notifys));
    }

    // 标记系统通知为已读
    @RequestMapping("/readNotify")
    public ResponseEntity readNotify(String id, HttpServletRequest req) {
        Integer res = pushMapper.readNotify(id);
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "标记已读失败"));
    }

    // 标记所有的系统通知为已读
    @RequestMapping("/allRead")
    public ResponseEntity allRead(HttpServletRequest req) {
        String token = req.getHeader(("token"));
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        Integer res = pushMapper.readAllNotify(userid);
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "标记所有通知已读失败"));
    }

    // 删除所有通知（个人中心的通知）
    @RequestMapping("/deleteAllNotify")
    public ResponseEntity deleteAllNotify(HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> resMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String usreid = (String) resMap.get("userid");
        int res = pushMapper.deleteAllNotify(usreid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 删除一则通知（个人中心的通知）
    @RequestMapping("/deleteOneNotify")
    public ResponseEntity deleteOneNotify(String id) {
        int res = pushMapper.deleteOneNotify(id);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }
}






























