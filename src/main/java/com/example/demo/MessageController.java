package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    // Route: GET '/'
    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    // Route: POST '/submit'
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submit(@RequestParam("new_message") String newMessage) {
        Message savedMessage = messageRepository.save(new Message(newMessage));

        Map<String, String> response = new HashMap<>();
        response.put("message", savedMessage.getMessage());
        return ResponseEntity.ok(response);
    }
}