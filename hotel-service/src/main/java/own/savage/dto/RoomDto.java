package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {

    private Long id;

    private Long number;

    private Long timesBooked;

    private Long hotelId;
}
