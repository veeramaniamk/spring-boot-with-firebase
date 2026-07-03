package com.veera.firebase.contorller;

import com.veera.firebase.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {


    @Autowired
    private KafkaProducerService producerService;

    @GetMapping("/send")
    public String send(@RequestParam("message") String message) {
        producerService.sendMessage(message);
        return "Message sent: " + message;
    }
}