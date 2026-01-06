package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomStatsDto {
    private Long id;
    private String number;
    private long timesBooked;
}
