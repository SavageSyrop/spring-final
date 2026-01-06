package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelDto {

    private Long id;

    private String name;

    private List<RoomDto> rooms;
}
