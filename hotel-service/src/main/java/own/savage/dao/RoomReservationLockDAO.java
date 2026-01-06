package own.savage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import own.savage.entities.RoomReservationLock;
import own.savage.entities.RoomStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomReservationLockDAO extends JpaRepository<RoomReservationLock, Long> {
    List<RoomReservationLock> findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long roomId,
            RoomStatus status,
            LocalDate endInclusive,
            LocalDate startInclusive
    );
}
