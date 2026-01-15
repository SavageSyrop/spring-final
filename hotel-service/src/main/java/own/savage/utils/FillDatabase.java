package own.savage.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import own.savage.entities.Hotel;
import own.savage.entities.Room;
import own.savage.entities.RoomReservationLock;
import own.savage.entities.RoomStatus;
import own.savage.service.HotelService;

import java.time.LocalDate;

@Service
public class FillDatabase implements CommandLineRunner {

    private final HotelService hotelService;

    public FillDatabase(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Override
    public void run(String... args) throws Exception {
        Hotel hotelCalifornia = new Hotel();
        hotelCalifornia.setName("California");

        Hotel hotelBaroness = new Hotel();
        hotelBaroness.setName("Baroness");

        hotelCalifornia = hotelService.saveHotel(hotelCalifornia);
        hotelBaroness = hotelService.saveHotel(hotelBaroness);

        Room roomCalifornia = new Room();
        roomCalifornia.setAvailable(true);
        roomCalifornia.setHotelId(hotelCalifornia.getId());
        roomCalifornia.setNumber(1L);
        roomCalifornia.setTimesBooked(54L);

        Room roomCalifornia2 = new Room();
        roomCalifornia2.setAvailable(true);
        roomCalifornia2.setHotelId(hotelCalifornia.getId());
        roomCalifornia2.setNumber(2L);
        roomCalifornia2.setTimesBooked(100L);

        Room roomCalifornia3 = new Room();
        roomCalifornia3.setAvailable(true);
        roomCalifornia3.setHotelId(hotelCalifornia.getId());
        roomCalifornia3.setNumber(1L);
        roomCalifornia3.setTimesBooked(4L);

        Room roomBaroness = new Room();
        roomBaroness.setAvailable(false);
        roomBaroness.setHotelId(hotelBaroness.getId());
        roomBaroness.setNumber(1L);
        roomBaroness.setTimesBooked(1L);

        Room roomBaroness2 = new Room();
        roomBaroness2.setAvailable(true);
        roomBaroness2.setHotelId(hotelBaroness.getId());
        roomBaroness2.setNumber(2L);
        roomBaroness2.setTimesBooked(22L);

        Room roomBaroness3 = new Room();
        roomBaroness3.setAvailable(true);
        roomBaroness3.setHotelId(hotelBaroness.getId());
        roomBaroness3.setNumber(12L);
        roomBaroness3.setTimesBooked(4L);

        roomCalifornia = hotelService.saveRoom(roomCalifornia);
        roomCalifornia2 = hotelService.saveRoom(roomCalifornia2);
        roomCalifornia3 = hotelService.saveRoom(roomCalifornia3);

        roomBaroness = hotelService.saveRoom(roomBaroness);
        roomBaroness2 = hotelService.saveRoom(roomBaroness2);
        roomBaroness3 = hotelService.saveRoom(roomBaroness3);

        RoomReservationLock roomReservationLockCalifornia1 = new RoomReservationLock();
        roomReservationLockCalifornia1.setStatus(RoomStatus.BUSY);
        roomReservationLockCalifornia1.setStartDate(LocalDate.now());
        roomReservationLockCalifornia1.setEndDate(LocalDate.now().plusDays(7));
        roomReservationLockCalifornia1.setRoom(roomCalifornia);

        RoomReservationLock roomReservationLockBaroness1 = new RoomReservationLock();
        roomReservationLockBaroness1.setStatus(RoomStatus.FREE);
        roomReservationLockBaroness1.setStartDate(LocalDate.now());
        roomReservationLockBaroness1.setEndDate(LocalDate.now().plusDays(7));
        roomReservationLockBaroness1.setRoom(roomBaroness);

        hotelService.saveRoomReservationLock(roomReservationLockCalifornia1);
        hotelService.saveRoomReservationLock(roomReservationLockBaroness1);
    }
}
