package own.savage.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import own.savage.dao.HotelDAO;
import own.savage.dao.RoomDAO;
import own.savage.dao.RoomReservationLockDAO;
import own.savage.entities.Hotel;
import own.savage.entities.Room;
import own.savage.entities.RoomReservationLock;
import own.savage.entities.RoomStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final HotelDAO hotelDao;
    private final RoomDAO roomDAO;
    private final RoomReservationLockDAO lockDAO;

    public HotelService(HotelDAO hotelDao, RoomDAO roomDAO, RoomReservationLockDAO lockDAO) {
        this.hotelDao = hotelDao;
        this.roomDAO = roomDAO;
        this.lockDAO = lockDAO;
    }

    @Transactional
    public List<Hotel> getAllHotels() {
        return hotelDao.findAll();
    }

    @Transactional
    public Optional<Hotel> getHotel(Long id) {
        return hotelDao.findById(id);
    }

    @Transactional
    public Hotel saveHotel(Hotel h) {
        return hotelDao.save(h);
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelDao.deleteById(id);
    }

    @Transactional
    public List<Room> getAllRoomsByHotelId(Long hotelId) {
        Optional<Hotel> hotelOptional = hotelDao.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            throw new EntityNotFoundException("No hotel by id " + hotelId);
        } else {
            return hotelOptional.get().getRooms();
        }
    }

    @Transactional
    public Optional<Room> getRoomById(Long roomId) {
        return roomDAO.findById(roomId);
    }

    @Transactional
    public Room saveRoom(Room r) {
        return roomDAO.save(r);
    }

    @Transactional
    public RoomReservationLock saveRoomReservationLock(RoomReservationLock r) {
        return lockDAO.save(r);
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomDAO.deleteById(id);
    }

    @Transactional
    public RoomReservationLock holdRoom(Long roomId, LocalDate startDate, LocalDate endDate) {

        List<RoomReservationLock> conflicts = lockDAO
                .findByRoomIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        roomId,
                        RoomStatus.FREE,
                        endDate,
                        startDate
                );

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Номер занят на указанный период");
        }

        RoomReservationLock lock = new RoomReservationLock();
        lock.setRoom(roomDAO.getReferenceById(roomId));
        lock.setStartDate(startDate);
        lock.setEndDate(endDate);
        lock.setStatus(RoomStatus.BUSY);
        return lockDAO.save(lock);
    }

    @Transactional
    public RoomReservationLock releaseHold(Long lockId) {
        RoomReservationLock lock = lockDAO.getReferenceById(lockId);
        if (lock.getStatus() == RoomStatus.BUSY) {
            lock.setStatus(RoomStatus.FREE);
        }
        return lockDAO.save(lock);
    }

    @Transactional
    public List<Room> getMostPopularRoomsInHotel(Long hotelId) {
        return roomDAO.findByHotelIdOrderByTimesBookedDesc(hotelId);
    }
}




