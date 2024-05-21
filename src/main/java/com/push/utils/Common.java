package com.push.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Common {

    public String readCon(String jsonString,String value) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        JsonNode baseNode = jsonNode.path("m2m:sgn").path("nev").path("rep").path("m2m:cin");
        JsonNode conNode = jsonNode.path("m2m:sgn").path("nev").path("rep").path("m2m:cin").path("con");
        JsonNode returnNode = conNode.path(value);
        JsonNode returnConNode = baseNode.path(value);
        String returnValue = objectMapper.writeValueAsString(returnNode);
        String returnConValue = objectMapper.writeValueAsString(returnConNode);


        // TODO: 추후 True/False 로 분기 할것
        if(value.equals("rsCf")) return returnValue;
        else if(value.equals("con")) return returnConValue;
        else return returnValue.replace("\"", "");
    }

}
