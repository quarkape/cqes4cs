package club.hue.config;

import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }


    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(defaultWebSecurityManager);
        /*
            anon: 无需认证就可以访问
            authc: 必须认证了才能访问
            user: 必须拥有 记住我 功能才能使用
            perms: 拥有对某个资源的访问权限才能做
            role: 拥有对某个角色权限才能访问
        */
        Map<String, String> filterChainDefinitionMap  = new LinkedHashMap<>();

        // 大部分角色都是用requireRoles注解的形式
        // 需要相应角色权限的资源
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }

    // 必须添加下面两个bean，才能使@RequiresRoles注解生效
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager); // 这里需要注入 SecurityManger 安全管理器
        return authorizationAttributeSourceAdvisor;
    }

    // 必须使用session管理器，才能够解决前后端分离shiro的subject未认证的问题
    @Bean
    public SessionManager sessionManager(){
        //将我们继承后重写的shiro session 注册
        ShiroSession shiroSession = new ShiroSession();
        //如果后续考虑多tomcat部署应用，可以使用shiro-redis开源插件来做session 的控制，或者nginx 的负载均衡
        shiroSession.setSessionDAO(new EnterpriseCacheSessionDAO());
        return shiroSession;
    }

    // 在这里将自定义的过滤器加上，可以解决使用PermissionString做权限的时候由于前后端分离导致的OPTIONS请求跨域的问题
    @Bean
    public FilterRegistrationBean replaceTokenFilter(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("MyOptionsFilter"); // spring 协助注入，注意名称不要重复
        registration.setFilter( new MyOptionsFilter());
        registration.setDispatcherTypes(DispatcherType.REQUEST); //拦截请求
        registration.addUrlPatterns("/*"); // 拦截请求url
        registration.setOrder(1); // 排序第一，避免影响。
        return registration;
    }
}
