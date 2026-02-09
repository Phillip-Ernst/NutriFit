package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.Gender;
import com.phillipe.NutriFit.model.UnitPreference;
import com.phillipe.NutriFit.model.entity.UserProfile;
import lombok.*;

import java.time.Instant;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String username;
    private Integer birthYear;
    private Integer age;
    private Gender gender;
    private UnitPreference unitPreference;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProfileResponse fromEntity(UserProfile profile, String username) {
        Integer age = null;
        if (profile.getBirthYear() != null) {
            age = Year.now().getValue() - profile.getBirthYear();
        }

        return ProfileResponse.builder()
                .id(profile.getId())
                .username(username)
                .birthYear(profile.getBirthYear())
                .age(age)
                .gender(profile.getGender())
                .unitPreference(profile.getUnitPreference())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
