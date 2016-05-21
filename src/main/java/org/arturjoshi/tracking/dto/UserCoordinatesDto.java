package org.arturjoshi.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.GregorianCalendar;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCoordinatesDto {
    private String username;
    private Double lat;
    private Double lon;
    private GregorianCalendar date;
}
