package cn.juhe.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${cas.server.url.prefix}")
    private String casServerUrl;


    @Value("${cas.client.url}")
    private String casClientUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("casServerUrl:{}", casServerUrl);
        log.info("casClientUrl:{}", casClientUrl);
        //首先检查，有没有登录过，从session中获取
        HttpSession session = request.getSession();
        Boolean isLogin = (Boolean) session.getAttribute("loginFlag");
        if (isLogin != null && isLogin) {
            //如果有session存在，证明已经登录成功了，则可以访问首页
            return true;
        } else {
            //如果没有登录过，但是请求携带了token，那么就判定为是用户认证中心发过来的
            String token = request.getParameter("token");
            log.info("token:{}", token);
            if (!StringUtils.isEmpty(token)) {
                //如果用户认证中心传过来了token，那么此时需要再去用户认证中心去验证这个token是否是正确的
                RestTemplate restTemplate = new RestTemplate();
                String forObject = restTemplate.getForObject(casServerUrl + "verify?token=" + token, String.class);
                //如果用户认证中心说这个token是正确的，那么就确定登录成功了，此时设置 session
                log.info("forObject:{}", forObject);
                if ("true".equals(forObject)) {
                    session.setAttribute("loginFlag", true);
                    return true;
                }
            }
            //如果session不存在，则让其跳转到用户认证中心，并且告诉用户认证中心，跳转回来到地址是我淘宝
            response.sendRedirect(casServerUrl + "checkLogin?redirectUrl=" + casClientUrl + "/taobao");
            return false;
        }

    }
}
