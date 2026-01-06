package own.savage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import own.savage.entity.Booking;

import java.util.List;

@Repository
public interface BookingDAO extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUsername(String username);
}


