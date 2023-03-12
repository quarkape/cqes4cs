package club.hue.controller;

import club.hue.mapper.PublishMapper;
import club.hue.utils.CheckXSSUtil;
import club.hue.utils.ResultVOUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

// 所有与信息发布功能相关的内容都放在这里面
// 包括教师发布通知、修改通知，学生获取通知等等

@RestController
public class PublishController {
    private final PublishMapper publishMapper;
    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;
    public PublishController(PublishMapper publishMapper) {
        this.publishMapper = publishMapper;
    }

    // 教师端 直接增加赛事
    @RequestMapping("/addContestOfManager")
    @RequiresRoles("manager")
    public ResponseEntity addContestOfManager(String name, String desc, String rule, HttpServletRequest req) {
        String contestTitle = publishMapper.checkContestDuplicate(name);
        if (contestTitle != null) {
            return ResponseEntity.ok().body(ResultVOUtil.error(1, "赛事已经在赛事库中"));
        }
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String curTime = "" + System.currentTimeMillis();
        Integer res = publishMapper.addContestOfManager(curTime, CheckXSSUtil.checkXSS(name), CheckXSSUtil.checkXSS(desc), userid, rule);
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "增加赛事失败"));
    }

    // 教学管理端获取所有的赛事
    @RequestMapping("/getAllContest")
    @RequiresRoles("manager")
    public ResponseEntity getAllContest(String currentPage, String pageSize) {
        Integer page = Integer.parseInt(currentPage);
        Integer size = Integer.parseInt(pageSize);
        List<HashMap<String, Object>> items = publishMapper.getAllContest((page - 1) * size, size);
        Integer total = publishMapper.getTotalContest();
        HashMap<String, Object> map = new HashMap<>();
        map.put("items", items);
        map.put("total", total);
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 教学管理端通过id删除赛事
    @RequestMapping("/deleteContest")
    @RequiresRoles("manager")
    public ResponseEntity deleteContest(String id) {
        Integer res = publishMapper.deleteContest(id);
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "删除赛事失败"));
    }

    // 教师在赛事管理页面获取某个赛事的详细信息
    @RequestMapping("/getContestDetailById")
    @RequiresRoles("manager")
    public ResponseEntity getContestDetailById(String id) {
        HashMap<String, Object> res = publishMapper.getContestDetailById(id);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 教师修改赛事内容
    @RequestMapping("/checkContestPassed")
    @RequiresRoles("manager")
    public ResponseEntity checkContestPassed( String title, String content, String ruleid, String id) {
        Integer res = publishMapper.checkContestPassed(CheckXSSUtil.checkXSS(title), CheckXSSUtil.checkXSS(content), ruleid, id);
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系开发者"));
    }
}







