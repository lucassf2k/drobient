package domain;

import java.util.UUID;

public record Drone(
        UUID id,
        Double atmosphericPressure,
        Double solarRadiation,
        Double temperature,
        Double humidity
) {
}
