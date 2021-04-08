package cn.juhe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/taobao")
    public String index() {
        System.out.println("taobao");
        return "taobao";
    }
}
