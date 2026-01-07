package own.savage.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import own.savage.entity.Booking;
import own.savage.entity.BookingStatus;
import own.savage.entity.Role;
import own.savage.entity.User;
import own.savage.service.BookingService;
import own.savage.service.UserService;

import java.time.LocalDate;

@Service
public class FillDatabase implements CommandLineRunner {

    private final UserService userService;
    private final BookingService bookingService;

    public FillDatabase(UserService userService, BookingService bookingService) {
        this.userService = userService;

        this.bookingService = bookingService;
    }

    @Override
    public void run(String... args) throws Exception {
        User userAdmin = new User();

        userAdmin.setPasswordHash("weewee");
        userAdmin.setRole(Role.ROLE_ADMIN);
        userAdmin.setUsername("dungeon_master");

        User userUser = new User();

        userUser.setPasswordHash("billyeah");
        userUser.setRole(Role.ROLE_USER);
        userUser.setUsername("billy");

        userService.save(userUser);
        userService.save(userAdmin);

        Booking bookingCalifornia = new Booking();
        Booking bookingBaroness = new Booking();

        bookingCalifornia.setUsername(userUser.getUsername());
        bookingCalifornia.setStatus(BookingStatus.CONFIRMED);
        bookingCalifornia.setRoomId(1L);
        bookingCalifornia.setStartDate(LocalDate.now());
        bookingCalifornia.setEndDate(LocalDate.now().plusDays(7));

        bookingBaroness.setUsername(userUser.getUsername());
        bookingBaroness.setStatus(BookingStatus.CANCELLED);
        bookingBaroness.setRoomId(1L);
        bookingBaroness.setStartDate(LocalDate.now());
        bookingBaroness.setEndDate(LocalDate.now().plusDays(7));

        bookingCalifornia = bookingService.save(bookingCalifornia);
        bookingBaroness = bookingService.save(bookingBaroness);
    }
}
