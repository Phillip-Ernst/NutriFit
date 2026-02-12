package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.entity.UserChangeHistory;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangeHistoryResponse {
    private Long id;
    private String entityType;
    private Long entityId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Instant changedAt;

    public static UserChangeHistoryResponse fromEntity(UserChangeHistory entity) {
        return UserChangeHistoryResponse.builder()
                .id(entity.getId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .fieldName(entity.getFieldName())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .changedAt(entity.getChangedAt())
                .build();
    }
}
