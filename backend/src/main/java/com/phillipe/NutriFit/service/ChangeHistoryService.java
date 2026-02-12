package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.response.UserChangeHistoryResponse;
import com.phillipe.NutriFit.model.entity.User;

import java.util.List;

public interface ChangeHistoryService {
    void recordChange(User user, String entityType, Long entityId, String field, Object oldVal, Object newVal);
    List<UserChangeHistoryResponse> getHistory(String username);
}
