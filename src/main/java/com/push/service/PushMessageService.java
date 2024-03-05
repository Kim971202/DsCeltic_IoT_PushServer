package com.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.push.controller.PushController;
import com.push.message.PushMessage;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PushMessageService {

     private final String API_URL = "https://fcm.googleapis.com/v1/projects/daesung-intergrate-iot/messages:send";
     private final ObjectMapper objectMapper;

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
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
        Response response = client.newCall(request).execute();

        if(response.body() != null) System.out.println(response.body().string());
     }

     private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
         PushMessage fcmMessage = PushMessage
                 .builder()
                 .message(
                         PushMessage
                                 .Message
                                 .builder()
                                 .token(targetToken)
                 .notification(
                         PushMessage
                                 .Notification
                                 .builder()
                                 .title(title)
                                 .body(body)
                                 .image(null)
                                 .build())
                                 .build())
                 .validate_only(false).build();

         return objectMapper.writeValueAsString(fcmMessage);
     }

    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "daesung-intergrate-iot-firebase-adminsdk-wilx9-0578b098b7.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        System.out.println("googleCredentials.getAccessToken().getTokenValue(): " + googleCredentials.getAccessToken().getTokenValue());
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
