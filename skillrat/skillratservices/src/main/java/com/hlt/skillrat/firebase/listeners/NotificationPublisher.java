package com.hlt.skillrat.firebase.listeners;




import com.hlt.skillrat.firebase.dto.Notification;
import com.hlt.skillrat.firebase.dto.NotificationEvent;
import com.hlt.skillrat.firebase.dto.NotificationEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
@Slf4j
public class NotificationPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	public NotificationPublisher(ApplicationEventPublisher eventPublisher) {
		this.applicationEventPublisher = eventPublisher;
	}

	public void sendNotifications(Notification data, final NotificationEventType eventType) {
		if (null == data || null == eventType) {
			return;
		}
		log.info("Sending notification asynchronously");
		NotificationEvent event = new NotificationEvent(this, List.of(data), eventType);
		applicationEventPublisher.publishEvent(event);
	}

	public void sendNotifications(List<Notification> data, final NotificationEventType eventType) {
		if (null == data || null == eventType || data.isEmpty()) {
			return;
		}
		log.info("Sending notifications asynchronously");
		NotificationEvent event = new NotificationEvent(this, data, eventType);
		applicationEventPublisher.publishEvent(event);
	}
}
