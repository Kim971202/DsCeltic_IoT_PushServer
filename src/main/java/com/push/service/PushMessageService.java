package com.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.push.config.PushMessage;
import com.push.constant.MessageBody;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PushMessageService {

     //private final String API_URL = "https://fcm.googleapis.com/v1/projects/daesung-intergrate-iot/messages:send";
     private final String API_URL = "https://fcm.googleapis.com/fcm/send";
     private final ObjectMapper objectMapper;
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

    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "daesung-intergrate-iot-firebase-adminsdk-wilx9-0578b098b7.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
