package com.interstore.interstore_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test/user")
    public String user() { return "Visibile a USER e ADMIN"; }

    @GetMapping("/api/test/admin")
    public String admin() { return "Visibile solo ad ADMIN"; }
}

