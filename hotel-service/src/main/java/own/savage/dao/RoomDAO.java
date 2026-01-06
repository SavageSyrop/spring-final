package own.savage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import own.savage.entities.Room;

@Repository
public interface RoomDAO extends JpaRepository<Room, Long> {
}
