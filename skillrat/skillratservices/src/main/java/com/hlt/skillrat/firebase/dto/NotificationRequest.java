package com.hlt.skillrat.firebase.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String body;
    private String topic;
    private String token;
}

