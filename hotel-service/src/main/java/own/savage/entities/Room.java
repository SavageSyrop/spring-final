package own.savage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Room extends AbstractEntity {

    private Long number;

    private boolean available;

    private Long timesBooked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hotel hotel;
}


