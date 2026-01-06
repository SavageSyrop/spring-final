package own.savage.controller;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.expression.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import own.savage.dto.BookingDto;
import own.savage.dto.RoomReservationDto;
import own.savage.dto.RoomStatsDto;
import own.savage.entity.Booking;
import own.savage.entity.BookingStatus;
import own.savage.service.BookingService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    public BookingController(BookingService bookingService, ModelMapper modelMapper) {
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingDto> create(@RequestBody RoomReservationDto roomReservationDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(convertToDto(bookingService.createBooking(userDetails.getUsername(), roomReservationDto.getRoomId(), roomReservationDto.getStartDate(), roomReservationDto.getEndDate())));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingDto> cancelBooking(@RequestBody BookingDto bookingDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Booking> booking = bookingService.getBookingById(bookingDto.getId());
        if (booking.isPresent()) {
            if (!Objects.equals(userDetails.getUsername(), booking.get().getUsername())) {
                throw new AccessDeniedException("Not your booking");
            } else {
                Booking bookingUpdated = booking.get();
                bookingUpdated.setStatus(BookingStatus.CANCELLED);
                return ResponseEntity.ok(convertToDto(bookingService.updateBooking(bookingUpdated)));
            }
        } else {
            throw new EntityNotFoundException("Booking " + bookingDto.getId() + " doesnt exist");
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> myBookings() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookingService.findAllByUsername(userDetails.getUsername())) {
            dtos.add(convertToDto(booking));
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/suggestions")
    public Mono<List<RoomStatsDto>> suggestions() {
        return bookingService.getPopularRooms();
    }

    private BookingDto convertToDto(Booking booking) throws ParseException {
        return modelMapper.map(booking, BookingDto.class);
    }

    private Booking convertToEntity(BookingDto bookingDto) throws ParseException {
        return modelMapper.map(bookingDto, Booking.class);
    }
}


