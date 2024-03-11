package com.push.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBody {

    @JsonProperty("to")
    private String destinationToken;
    private String priority;
    private Body data;

    @Getter
    @Setter
    public static class Body{
        private String title;
        private String body;
    }

}
