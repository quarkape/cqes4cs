package club.hue.config;

import club.hue.mapper.LoginMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    LoginMapper loginMapper;

    // 进入设置了拦截的页面就会触发这个方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        HashMap<String, Object> obj = (HashMap<String, Object>) subject.getPrincipal();
        String role = (String) obj.get("role");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> set = new HashSet<>();
        info.setStringPermissions(set);
        info.addRole(role);
        return info;
    }

    // LoginController里面的checkLogin中的currentUser.login()会触发这个函数
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        String userid = userToken.getUsername();
        HashMap<String, Object> user = loginMapper.getUserByUserid(userid);
        if (user == null) {
            return null;
        }
        return new SimpleAuthenticationInfo(user, user.get("password"), userToken.getUsername());
    }
}
