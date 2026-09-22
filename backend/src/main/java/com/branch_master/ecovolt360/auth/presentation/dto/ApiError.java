package com.branch_master.ecovolt360.auth.presentation.dto;

import java.util.Map;

public record ApiError(
        String code,
        String message,
        Map<String, String> fieldErrors
) {
}