package cn.juhe.controller;

import cn.juhe.db.MockDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
@Slf4j
public class ServerController {

    @RequestMapping("/index")
    public String index(String redirectUrl, Model model) {
        return "login";
    }

    @RequestMapping("/login")
    public String login(String username, String password, String redirectUrl, HttpSession session, Model model) {
        //校验用户名和密码是否合法，如果合法，则发送一个令牌给这个用户，这个用户收到令牌之后，再过来请求看看这个令牌是否正确
        if ("admin".equals(username) && "admin".equals(password)) {
            //发放一个ticket
            log.info("用户名密码验证成功。。。");
            String token = UUID.randomUUID().toString();
            session.setAttribute("token", token);
            MockDB.T_TOKEN.add(token);//把这个令牌存到数据库当中
            //保存这个用户的session
            //如果登录成功了，则需要重定向到来时候到路，并且携带上这个生成到ticket，
            model.addAttribute("token", token);
            log.info("ticket:{}", token);
            System.out.println("redirectUrl:" + redirectUrl);
            return "redirect:" + redirectUrl + "?token=" + token;
        }
        model.addAttribute("redirectUrl", redirectUrl);
        return "login";

    }

    @RequestMapping("checkLogin")
    public String checkLogin(String redirectUrl, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        log.info("checkLogin.token:{}", token);
        if (StringUtils.isEmpty(token)) {
            //如果token是空，那么这个请求肯定是没有登录成功过的，则引导其去登录页面
            model.addAttribute("redirectUrl", redirectUrl);
            return "login";
        }
        model.addAttribute("token", token);
        //如果不为空，则这个请求从哪里来，就让他回到哪里去
        return "redirect:" + redirectUrl + "?token=" + token;
    }

    @RequestMapping("/verify")
    @ResponseBody
    public String verifyToken(String token) {
        if (MockDB.T_TOKEN.contains(token)) {
            return "true";
        }
        return "false";
    }
}
