package com.hlt.usermanagement.dto.enums;

public enum ApplicationStatus {

    PENDING,        // Application submitted but not yet reviewed
    UNDER_REVIEW,   // Recruiter/Admin is reviewing
    SHORTLISTED,    // Applicant moved to next round
    INTERVIEW,      // Interview scheduled
    SELECTED,       // Final selection done
    REJECTED,       // Not selected
    WITHDRAWN       // Applicant withdrew the application
}
