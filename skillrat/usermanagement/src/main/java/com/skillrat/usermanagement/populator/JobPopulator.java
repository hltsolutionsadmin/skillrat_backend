package com.skillrat.usermanagement.populator;

import org.springframework.stereotype.Component;

import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.model.JobModel;
import com.skillrat.utils.Populator;

@Component
public class JobPopulator implements Populator<JobModel, JobDTO> {

    @Override
    public void populate(JobModel source, JobDTO target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId());
        target.setCompanyName(source.getCompanyName());
        target.setPosition(source.getPosition());
        target.setDescription(source.getDescription());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
    }

    public JobDTO toDTO(JobModel source) {
        if (source == null) {
            return null;
        }
        JobDTO dto = new JobDTO();
        populate(source, dto);
        return dto;
    }
}
