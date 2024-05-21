package com.push.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "push.message")
public class PushMessage {

    private String brightnessControlTitle;
    private String brightnessControlBody;
    private String householdNoti1302Message;
    private String noticeTitle;
    private String noticeBody;

}
