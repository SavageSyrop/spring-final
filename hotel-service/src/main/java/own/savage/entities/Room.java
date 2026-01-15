package own.savage.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Room extends AbstractEntity {

    private Long number;

    private boolean available;

    private Long timesBooked;

    @Column(name = "hotel_id")
    private Long hotelId;
}


