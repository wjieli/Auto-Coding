package com.osp.hpe.lwj.controller;

import com.osp.hpe.lwj.service.AutoCodingService;
import com.osp.hpe.lwj.vo.Para;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lwenjie on 2017/6/9.
 */
@RestController
public class AutoCodingController {

    @Autowired
    private AutoCodingService service;

    @RequestMapping(value = "/auto/coding")
    public Object autoCoding(@RequestBody Para para){
        service.autoCoding(para);
        return "success";
    }
}
