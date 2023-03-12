package club.hue.controller;

import club.hue.mapper.ManagementMapper;
import club.hue.mapper.PushMapper;
import club.hue.utils.CheckXSSUtil;
import club.hue.utils.DateTransition;
import club.hue.utils.Md5Util;
import club.hue.utils.ResultVOUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

// 所有与学生管理相关的内容都放在这里面
// 包括重置学生密码，学生修改自己的密码和信息等
@RestController
public class ManagementController {
    @Autowired
    private ManagementMapper managementMapper;
    @Autowired
    private PushMapper pushMapper;
    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    // 修改自己的密码
    @RequestMapping("/modifyPassword")
    public ResponseEntity modifyPassword(String password, HttpServletRequest req) {
        String token = req.getHeader("token");
        // 从redis中以token为名字查找绑定的用户信息
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String encodedPassword = (new Md5Util()).getMd5Code(password);
        Integer res = managementMapper.changePassword(encodedPassword, userid);
        if (res != -1) {
            redisTemplate.delete(token);
            long curTime = System.currentTimeMillis();
            String title = "修改密码通知";
            String detail = "您的账号于" + DateTransition.DateMills2String(curTime) + "修改了密码。如果不是您本人操作，请联系教师恢复为初始密码。";
            pushMapper.addOneNotify(curTime + "" + userid, title, detail);
            return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        }
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "修改密码失败"));
    }

    // 教师端重置学生的密码
    @RequestMapping("/resetPassword")
    @RequiresRoles(value = {"system", "manager"},logical = Logical.OR)
    public ResponseEntity resetPassword(String id) {
        String code = randomCode();
        String newPwd = new Md5Util().getMd5Code(code);
        Integer res = managementMapper.resetPassword(newPwd, id);
        if (res != -1) {
            String title = "密码重置通知";
            long curTime = System.currentTimeMillis();
            String detail = "你的密码已经由教师在"+DateTransition.DateMills2String(curTime)+"重置，重置后的初始密码为: " + code + "(区分大小写)。如有需要，请尽快修改密码。";
            pushMapper.addOneNotify(curTime+id,title,detail);
            return ResponseEntity.ok().body(ResultVOUtil.success(code));
        }
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "重置密码失败!"));
    }

    // 管理员修改教师的密码
    @RequestMapping("/resetPasswordManager")
    @RequiresRoles("system")
    public ResponseEntity resetPasswordManager(String userid) {
        String code = randomCode();
        String newPwd = new Md5Util().getMd5Code(code);
        Integer res = managementMapper.resetPassword(newPwd, userid);
        if (res != -1) {
            String title = "密码重置通知";
            long curTime = System.currentTimeMillis();
            String detail = "你的密码已经由管理员在"+DateTransition.DateMills2String(curTime)+"重置，重置后的登录密码为: " + code + "(区分大小写)。如有需要，请尽快修改密码。";
            pushMapper.addOneNotify(curTime+userid,title,detail);
            return ResponseEntity.ok().body(ResultVOUtil.success(code));
        }
        return ResponseEntity.ok().body(ResultVOUtil.error(0, "重置密码失败!"));
    }

    // 获取自己的基本信息
    @RequestMapping("/getUserBasicInfo")
    public ResponseEntity getUserBasicInfo(HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String role = (String) redisMap.get("role");
        if (role.equals("manager")) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userid", userid);
            map.put("role", role);
            return ResponseEntity.ok().body(ResultVOUtil.success(map));
        }
        HashMap<String, Object> map = managementMapper.getUserBasicInfo(userid);
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 教师端 学生管理页面获取所有学生的列表
    @RequestMapping("/getAllStudentList")
    @RequiresRoles("manager")
    public ResponseEntity getAllStudentList(String major, String curPage, String pageSize, HttpServletRequest req) {
        String token = req.getHeader("token");
        // 从redis中以token为名字查找绑定的用户信息
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        int curPageInt = Integer.parseInt(curPage);
        int pageSizeInt = Integer.parseInt(pageSize);
        List<HashMap<String, Object>> res = managementMapper.getAllStudentList(userid, major, (curPageInt-1) * pageSizeInt, pageSizeInt);
        int total = managementMapper.getStudentNumByMajor(userid, major);
        HashMap resMap = new HashMap();
        resMap.put("item", res);
        resMap.put("total", total);
        return ResponseEntity.ok().body(ResultVOUtil.success(resMap));
    }

    // 手动给学生加分或者减分，增加一项
    @RequestMapping("/modifyScoreManually")
    @RequiresRoles("manager")
    public ResponseEntity modifyScoreManually(String id, String year, String type, String score, String desc) {
        String uuid = UUID.randomUUID() + "";
        managementMapper.modifyScoreManually(uuid, id, year, Double.parseDouble(score), Integer.parseInt(type), CheckXSSUtil.checkXSS(desc));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 删除教师账号
    @RequestMapping("/deleteAccountManager")
    @RequiresRoles("system")
    public ResponseEntity deleteAccountManager(String userid) {
        int res = managementMapper.deleteAccountManager(userid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 修改管理员管理的年级
    @RequestMapping("/alterManagerYear")
    @RequiresRoles("system")
    public ResponseEntity alterManagerYear(String year, String userid) {
        int res = managementMapper.alterManagerYear(userid, year);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 新增教师账号
    @RequestMapping("/addManagerAccount")
    @RequiresRoles("system")
    public ResponseEntity addManagerAccount(String name, String userid, String password, String year) {
        String md5Password = new Md5Util().getMd5Code(password);
        managementMapper.addManagerToUsers(userid, md5Password);
        managementMapper.addManagerToUserInfo(userid, CheckXSSUtil.checkXSS(name), year);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 生成随机码
    private String randomCode() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String code = "";
        for (int i=0;i<6;i++) {
            code += str.charAt((int) Math.floor(Math.random()*62));
        }
        return code;
    }

}






























