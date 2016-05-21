package org.arturjoshi.tracking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCoordinatesDto {
    @JsonIgnore
    private String user_id;
    private Double lat;
    private Double lon;
    private String date;
}
