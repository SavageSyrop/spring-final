package own.savage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import own.savage.entities.Hotel;

@Repository
public interface HotelDAO extends JpaRepository<Hotel, Long> {
}
