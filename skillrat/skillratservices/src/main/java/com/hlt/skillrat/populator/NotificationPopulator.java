package com.hlt.skillrat.populator;


import com.hlt.commonservice.dto.NotificationDTO;
import com.hlt.skillrat.model.NotificationModel;
import com.hlt.utils.Populator;
import org.springframework.stereotype.Component;

@Component
public class NotificationPopulator implements Populator<NotificationModel, NotificationDTO> {

    @Override
    public void populate(NotificationModel source, NotificationDTO target) {
        target.setId(source.getId());
        target.setCreationTime(source.getCreationTime());
        target.setMessage(source.getMessage());
        target.setModificationTime(source.getModificationTime());
        target.setTitle(source.getTitle());
        target.setType(source.getType().name().toLowerCase().replace("_", " "));

        if (null != source.getUserId()) {
            target.setUserId(source.getUserId());
        }

    }

}
