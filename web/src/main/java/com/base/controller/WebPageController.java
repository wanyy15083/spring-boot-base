package com.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Controller
public class WebPageController {

    @RequestMapping(value = "/index")
    String index(Model model) {
        model.addAttribute("name", "Coder");
        List<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        model.addAttribute("projects", list);
        return "index";
    }

    @RequestMapping(value = "/testjsp")
    String testJspPage() {
        return "test";
    }
}
