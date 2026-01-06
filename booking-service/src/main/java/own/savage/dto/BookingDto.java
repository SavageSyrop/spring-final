package own.savage.dto;

import lombok.Getter;
import lombok.Setter;
import own.savage.entity.BookingStatus;

import java.time.LocalDate;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private String username;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
}


