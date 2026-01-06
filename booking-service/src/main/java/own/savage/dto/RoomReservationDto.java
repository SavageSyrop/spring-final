package own.savage.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RoomReservationDto {

    private Long id;

    private Long roomId;

    private LocalDate startDate;

    private LocalDate endDate;
}
