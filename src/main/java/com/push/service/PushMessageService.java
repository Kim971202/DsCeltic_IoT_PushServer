package com.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.push.constant.FCMMessageDto;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PushMessageService {

    private final ObjectMapper objectMapper;
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/daesungiotcontrol/messages:send";
    public PushMessageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 알림 푸쉬를 보내는 역할을 하는 메서드
     * @param targetToken : 푸쉬 알림을 받을 클라이언트 앱의 식별 토큰
     * */
    public void sendMessageTo(String targetToken, String title, String body, String id, String description, String pushYn) throws IOException{

        String message = makeMessage(targetToken, title, body, id, description, pushYn);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+ getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        if(response.body() != null) log.info(response.body().string());
    }


    /**
     * makeMessage : 알림 파라미터들을 FCM이 요구하는 body 형태로 가공한다.
     * @param targetToken : firebase token
     * @param title : 알림 제목
     * @param body : 알림 내용
     * @return
     * */
    private String makeMessage(String targetToken, String title, String body, String name, String description, String pushYn) throws JsonProcessingException {

        FCMMessageDto fcmMessage = FCMMessageDto.builder()
                .message(
                        FCMMessageDto.Message.builder()
                                .token(targetToken)
//                                .notification(
//                                        FCMMessageDto.Notification.builder()
//                                                .title(title)
//                                                .body(body)
//                                                .build()
//                                )
                                .data(
                                        FCMMessageDto.Data.builder()
                                                .name(name)
                                                .description(description)
                                                .pushYn(pushYn)
                                                .build()
                                )
                                .build()
                )
                .validateOnly(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        // firebase 에서 access token 취득
        GoogleCredentials   googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("daesungiotcontrol-firebase-adminsdk-zt3g6-6e751330cd.json").getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
