package com.skillrat.utils;

/**
 * Global constants for the application.
 * Use SRAppConstants to keep all fixed values in one place.
 */
public interface SRAppConstants {

    // =======================================
    // General Constants
    // =======================================
    String _0 = "0";
    String _20 = "20";
    String OR = "or";
    String MULTIBLOCK = "MULTIBLOCK";
    String SINGLE = "SINGLE";
    String BEARER = "Bearer ";
    String DASHBOARD = "mediaType";

    // =======================================
    // Pagination / Response Metadata
    // =======================================
    String TOTAL_PAGES = "totalPages";
    String TOTAL_ITEMS = "totalItems";
    String CURRENT_PAGE = "currentPage";
    String PAGE_NUM = "pageNo";
    String PAGE_SIZE = "pageSize";
    String PROFILES = "data";

    // =======================================
    // Roles (Spring Security Expressions)
    // =======================================
    String ROLE_USER = "hasRole('ROLE_USER')";
    String ROLE_STUDENT = "hasRole('ROLE_STUDENT')";
    String ROLE_USER_ADMIN = "hasRole('ROLE_USER_ADMIN')";
    String ROLE_ROLE_BUILDER = "hasRole('ROLE_BUILDER')";
    String ROLE_LAND_OWNER = "hasRole('ROLE_LAND_OWNER')";
    String ROLE_SUPER_ADMIN = "hasRole('ROLE_SUPER_ADMIN')";
    String ROLE_HOSPITAL_ADMIN = "hasRole('ROLE_HOSPITAL_ADMIN')";
    String ROLE_DOCTOR = "hasRole('ROLE_DOCTOR')";
    String ROLE_RECEPTIONIST = "hasRole('ROLE_RECEPTIONIST')";
    String ROLE_HOSPITAL_MANAGER = "hasRole('ROLE_HOSPITAL_MANAGER')";
    String ROLE_TELECALLER = "hasRole('ROLE_TELECALLER')";
    String ROLE_CALL_SUPERVISOR = "hasRole('ROLE_CALL_SUPERVISOR')";

    // =======================================
    // Direct Role Names (Without hasRole())
    // =======================================
    String ROLE_BUILDER = "ROLE_BUILDER";
    String ROLE_USER_ADMINS = "ROLE_USER_ADMIN";

    // =======================================
    // Statuses
    // =======================================
    String VERIFIED = "Verified";
    String FAILED = "FAILED";
    String SUCCESS = "Success";
    String PENDING = "PENDING";
    String REFUNDED = "REFUNDED";
    String PROCESSING = "PROCESSING";

    // =======================================
    // Application Messages (Optional)
    // You can add global success/failure messages here
    // =======================================
    String REQUIREMENT_CREATE_SUCCESS = "Requirement created successfully";
    String REQUIREMENT_FETCH_SUCCESS = "Requirement fetched successfully";
    String REQUIREMENT_LIST_SUCCESS = "Requirements listed successfully";
    String REQUIREMENT_UPDATE_SUCCESS = "Requirement updated successfully";
    String REQUIREMENT_DELETE_SUCCESS = "Requirement deleted successfully";

    String APPLICATION_CREATE_SUCCESS = "Application created successfully";
    String APPLICATION_FETCH_SUCCESS = "Application fetched successfully";
    String APPLICATION_LIST_SUCCESS = "Applications listed successfully";
    String APPLICATION_UPDATE_SUCCESS = "Application updated successfully";
    String APPLICATION_DELETE_SUCCESS = "Application deleted successfully";
}


