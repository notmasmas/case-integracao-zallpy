package com.branch_master.ecovolt360.messages.application.dto;

import java.util.List;

public record MessagePageDTO(
        List<MessageDTO> messages,
        int page,
        int totalPages,
        int totalMessages
) {
}
