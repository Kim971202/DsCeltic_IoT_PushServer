package com.push.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.push.service.PushMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping("/push/v1")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PushController {

    @Autowired
    PushMessageService pushMessageService;

    @PostMapping("/myPost")
    @ResponseBody
    public void msgController(HttpSession session, HttpServletRequest request, @ModelAttribute String params, HttpServletResponse response)
        throws Exception {

        pushMessageService.sendMessageTo("0578b098b708c6aca7883ceebb6010ba37e4d8ab", "ThisisTitle", "ThisisBody");
    }

}