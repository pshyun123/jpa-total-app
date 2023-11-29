package com.kh.jpatotalapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @PostMapping("/python")
    public String testPython(@RequestBody Map<String, String> data) {
        log.warn("data : {}", data);
        return data.get("id");
    }
}
