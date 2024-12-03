package com.push.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.push.config.PushMessage;
import com.push.service.PushMessageService;
import com.push.utils.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@RequestMapping("/push/v1")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PushController {

    @Autowired
    PushMessageService pushMessageService;
    @Autowired
    Common common;

    @PostMapping("/AppServerToPushServer")
    @ResponseBody
    public void msgController(@RequestBody String jsonBody) throws Exception {

        /*
        * 필요한 변수
        * 1. targetToken: APP 토큰
        * 2. Title
        * 3. Body
        * 4. Id
        * 5. isEnd
        * */

        System.out.println("jsonBody: " + jsonBody);
        String targetToken = common.readCon(jsonBody, "targetToken");

        System.out.println("targetToken: " + targetToken);

        pushMessageService.sendMessageTo(targetToken, common.readCon(jsonBody, "con"));
    }

}
