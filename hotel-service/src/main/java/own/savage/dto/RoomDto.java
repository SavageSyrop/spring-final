package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDto {

    private Long id;

    private String number;

    private boolean rentable;

    private Long hotelId;
}
