package club.hue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

// 设置拦截器后的放行配置文件
// 必须添加@Configuration注解
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    @Bean
    public AuthenticationInterceptor getAuthenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    //请求拦截
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 先拦截所有请求
        registry.addInterceptor(getAuthenticationInterceptor())
                .addPathPatterns("/**")
                //对登陆页面和静态资源进行放行
                .excludePathPatterns("/checkLogin", "/files/**");
    }

    /*
     * 这里主要为了解决跨域问题,重写addCorsMappings方法
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST","PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options")
                .allowCredentials(false).maxAge(3600);
        super.addCorsMappings(registry);
    }

    // 必须要自定义资源文件路径
    // 配置虚拟路径
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 加分图片，上传的学生名单、上传的成绩表格，上传的加分文件，导出的评价结果等静态资源目录
        // registry.addResourceHandler("/files/**").addResourceLocations("/www/apps/cqes4cs/files/");
        registry.addResourceHandler("/files/**").addResourceLocations("file:G:/Projects/Materials/cqes4cs/files/");
        super.addResourceHandlers(registry);
    }
}