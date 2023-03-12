package club.hue.controller;

import club.hue.mapper.LoginMapper;
import club.hue.utils.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class LoginController {

    @Autowired
    private LoginMapper loginMapper;

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    // 所有角色的登录验证
    @RequestMapping("/checkLogin")
    public ResponseEntity checkLogin(String serviceId) throws UnsupportedEncodingException {
        String userid = new Base64Dec().getBase64Res(serviceId)[0];
        String password = new Base64Dec().getBase64Res(serviceId)[1];
        // 封装用户数据，准备shiro登录
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken userToken = new UsernamePasswordToken(userid, new Md5Util().getMd5Code(password));
        try {
            // 进入shiro登录
            currentUser.login(userToken);
            // 将用户token和角色存入redis并设置redis有效期为30分钟
            HashMap<String, Object> userMap = loginMapper.getUserByUserid(userid);
            String tokenId = UUID.randomUUID() + "";
            HashMap<String, Object> authMap = new HashMap<>();
            authMap.put("userid", userid);
            authMap.put("role", userMap.get("role"));
            authMap.put("token", tokenId);
            redisTemplate.opsForValue().set(tokenId, authMap, Duration.ofMinutes(60L));
            // 将token，角色，shiro session id信息返回给客户端
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", tokenId);
            map.put("role", userMap.get("role"));
            // shiro的sessionID
            String authToken = (String) currentUser.getSession().getId();
            map.put("authToken", authToken);
            return ResponseEntity.ok().body(ResultVOUtil.success(map));
            // 处理用户名和密码检验中的各种异常情况
        } catch (UnknownAccountException uae) {
            return ResponseEntity.status(401).body(ResultVOUtil.error(1, "当前用户不存在"));
        } catch (IncorrectCredentialsException ice) {
            return ResponseEntity.status(403).body(ResultVOUtil.error(2, "密码错误"));
        } catch (LockedAccountException lae) {
            return ResponseEntity.ok().body(ResultVOUtil.error(3, "用户被锁定，请联系管理员"));
        } catch (AuthenticationException ae) {
            return ResponseEntity.ok().body(ResultVOUtil.error(4, "未知错误，请联系管理员"));
        }
    }

    // 登陆后获取所有未读通知的数量
    @RequestMapping("/getUnreadNotifyCount")
    @RequiresRoles(value = {"manager", "student"}, logical = Logical.OR)
    public ResponseEntity getUnreadNotifyCount(HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        HashMap<String, Object> map = new HashMap<>();
        map.put("unReadCount", loginMapper.getUnreadNotifyCount(userid));
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 所有角色的登出操作
    @RequestMapping("/logOut")
    public ResponseEntity logOut(HttpServletRequest req) {
        String token = req.getHeader("token");
        redisTemplate.delete(token);
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }
}
