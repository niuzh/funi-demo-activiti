package com.funi.demo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登录业务控制器
 * @author zhihuan.niu
 */
@Controller
public class LoginController {

    /*
    * ModelAndView 包括视图信息和渲染视图的模型数据信息
    * 数据模型对象放置到request的属性中
    * */
    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("userName","niu");
        return "index";
    }
}
