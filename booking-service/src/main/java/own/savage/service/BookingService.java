package own.savage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import own.savage.dao.BookingDAO;
import own.savage.dto.RoomStatsDto;
import own.savage.entity.Booking;
import own.savage.entity.BookingStatus;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingDAO bookingDAO;
    private final WebClient webClient;
    private final int retries;
    private final Duration timeout;

    public BookingService(
            BookingDAO bookingDAO,
            WebClient.Builder builder,
            @Value("${hotelService.url}") String hotelUrl,
            @Value("${hotelService.timeout}") int timeoutMs,
            @Value("${hotelService.retries}") int retries
    ) {
        this.bookingDAO = bookingDAO;
        this.webClient = builder.baseUrl(hotelUrl).build();
        this.retries = retries;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Transactional
    public Booking createBooking(String username, Long roomId, LocalDate start, LocalDate end) {
        Booking booking = new Booking();
        booking.setUsername(username);
        booking.setRoomId(roomId);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingDAO.save(booking);

        Map<String, String> payload = Map.of(
                "startDate", start.toString(),
                "endDate", end.toString()
        );

        callHotel("/api/rooms/" + roomId + "/hold", payload).block(timeout);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingDAO.save(booking);
    }

    private Mono<String> callHotel(String path, Map<String, String> payload) {
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(timeout)
                .retryWhen(Retry.backoff(retries, Duration.ofMillis(600)).maxBackoff(Duration.ofSeconds(4)));
    }

    public Mono<List<RoomStatsDto>> getPopularRooms() {
        return webClient.get()
                .uri("/hotels/rooms")
                .retrieve()
                .bodyToFlux(RoomStatsDto.class)
                .collectList()
                .map(list -> list.stream()
                        .sorted(java.util.Comparator.comparingLong(RoomStatsDto::getTimesBooked)
                                .thenComparing(RoomStatsDto::getId))
                        .toList());
    }

    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingDAO.findById(bookingId);
    }

    public Booking updateBooking(Booking booking) {
        return bookingDAO.save(booking);
    }

    public List<Booking> findAllByUsername(String username) {
        return bookingDAO.findAllByUsername(username);
    }
}


