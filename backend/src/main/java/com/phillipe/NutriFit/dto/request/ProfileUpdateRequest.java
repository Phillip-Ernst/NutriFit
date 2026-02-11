package com.phillipe.NutriFit.dto.request;

import com.phillipe.NutriFit.model.Gender;
import com.phillipe.NutriFit.model.UnitPreference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @Min(value = 1900, message = "Birth year must be after 1900")
    @Max(value = 2025, message = "Birth year cannot be in the future")
    private Integer birthYear;

    private Gender gender;

    private UnitPreference unitPreference;
}
