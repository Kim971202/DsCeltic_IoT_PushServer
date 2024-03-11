package com.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.push.config.PushMessage;
import com.push.constant.MessageBody;
import com.push.constant.FCMMessageDto;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PushMessageService {

    private final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/daesung-intergrate-iot/messages:send";
     @Autowired
     private PushMessage pushMessage;

     @Value("${server.authorization.token}")
     private String serverAuthorizationToken;

     @Value("${client.destination.token}")
     private String clientDestinationToken;

    public PushMessageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        System.out.println("message: " + message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "key=" + serverAuthorizationToken)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
        Response response = client.newCall(request).execute();

        if(response.body() != null) System.out.println("response.body().string(): " + response.body().string());
    }

     public String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {

         MessageBody messageBody = new MessageBody();
         MessageBody.Body myBody = new MessageBody.Body();

         messageBody.setDestinationToken(clientDestinationToken);
         messageBody.setPriority("high");
         myBody.setTitle(pushMessage.getNoticeTitle());
         myBody.setBody(pushMessage.getNoticeBody());
         messageBody.setData(myBody);

         System.out.println("objectMapper.writeValueAsString(messageBody): " + objectMapper.writeValueAsString(messageBody));
         return objectMapper.writeValueAsString(messageBody);
     }

    /**
     * 알림 푸쉬를 보내는 역할을 하는 메서드
     * @param targetToken : 푸쉬 알림을 받을 클라이언트 앱의 식별 토큰
     * */
    public void sendMessageTo(String targetToken, String title, String body, String id, String isEnd) throws IOException{

        String message = makeMessage(targetToken, title, body, id, isEnd);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+ getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        log.info(response.body().string());
        return;
    }


    /**
     * makeMessage : 알림 파라미터들을 FCM이 요구하는 body 형태로 가공한다.
     * @param targetToken : firebase token
     * @param title : 알림 제목
     * @param body : 알림 내용
     * @return
     * */
    public String makeMessage(String targetToken, String title, String body, String name, String description) throws JsonProcessingException {

        FCMMessageDto fcmMessage = FCMMessageDto.builder()
                .message(
                        FCMMessageDto.Message.builder()
                                .token(targetToken)
                                .notification(
                                        FCMMessageDto.Notification.builder()
                                                .title(title)
                                                .body(body)
                                                .build()
                                )
                                .data(
                                        FCMMessageDto.Data.builder()
                                                .name(name)
                                                .description(description)
                                                .build()
                                )
                                .build()
                )
                .validateOnly(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        // firebase로 부터 access token을 가져온다.
        GoogleCredentials   googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("daesung-intergrate-iot-firebase-adminsdk-wilx9-0578b098b7.json").getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
