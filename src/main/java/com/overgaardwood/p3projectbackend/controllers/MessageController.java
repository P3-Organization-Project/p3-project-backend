package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World this is working now");
    }
}
