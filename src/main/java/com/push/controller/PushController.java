package com.push.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.push.config.PushMessage;
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

        pushMessageService.sendMessageTo("excnxsSdTCuDA_My9rKj8X:APA91bHsLAkbFFDMmujoEDTeHN-ZXPzVyCgFkkhgXSz1cGC-QtveSeX86KZdtiDbYGDoYbqhLdjaZx_6Q8WdUyb9MyoArzc8-aos6kixKN9-hH72_A_PKWxXeBK1_GQNbM_IdmW-lyBR",
                "myTitle", "myBody", "myId", "ThisEnd");
    }

}
