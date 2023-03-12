package club.hue.resolver;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述：处理用户没有权限访问的错误
 *
 * @author caojing
 * @create 2019-01-27-17:12
 */
@ControllerAdvice
public class NoPermissionException {
    // 没有权限时抛出的异常
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public void handleShiroException(HttpServletResponse resp) throws IOException {
        resp.setStatus(601);
        resp.getWriter().append("当前用户无权进行该请求!");
    }

    // 权限校验失败时抛出的异常
    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public void AuthorizationException(HttpServletResponse resp) throws IOException {
        resp.setStatus(602);
        resp.getWriter().append("当前用户请求权限验证失败，请登出后再试!");
    }
}
