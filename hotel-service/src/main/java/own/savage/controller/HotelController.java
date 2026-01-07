package own.savage.controller;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import own.savage.dto.HotelDto;
import own.savage.dto.RoomDto;
import own.savage.entities.Hotel;
import own.savage.entities.Room;
import own.savage.service.HotelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    @Autowired
    private final HotelService hotelService;
    @Autowired
    private ModelMapper modelMapper;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelDto> getAllHotels() {
        List<HotelDto> dtos = new ArrayList<>();
        for (Hotel hotel : hotelService.getAllHotels()) {
            dtos.add(convertToDto(hotel));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelService.getHotel(id);

        if (hotel.isPresent()) {
            return ResponseEntity.ok(convertToDto(hotel.get()));
        } else {
            throw new EntityNotFoundException("Hotel " + id + " not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public HotelDto createHotel(@RequestBody HotelDto hotelDto) {
        Hotel hotel = convertToEntity(hotelDto);
        hotel.setRooms(new ArrayList<>());
        return convertToDto(hotelService.saveHotel(hotel));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id, @RequestBody HotelDto dto) {
        return hotelService.getHotel(id)
                .map(existing -> {
                    HotelDto hotelDto = convertToDto(hotelService.saveHotel(convertToEntity(dto)));
                    return ResponseEntity.ok(hotelDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms")
    public List<RoomDto> getAllRoomsInHotel(@RequestParam Long hotelId) {
        Optional<Hotel> hotel = hotelService.getHotel(hotelId);

        if (hotel.isPresent()) {
            return hotel.get().getRooms().stream().map(e -> modelMapper.map(e, RoomDto.class)).toList();
        } else {
            throw new EntityNotFoundException("Hotel " + hotelId + " not found");
        }
    }

    @PostMapping("/{hotelId}/rooms/popular")
    public List<RoomDto> getPopilarRooms(@PathVariable Long hotelId) {
        List<RoomDto> dtos = new ArrayList<>();
        List<Room> popularRooms = hotelService.getMostPopularRoomsInHotel(hotelId);

        for (Room room : popularRooms) {
            dtos.add(convertToDto(room));
        }
        return dtos;
    }

    private HotelDto convertToDto(Hotel hotel) throws ParseException {
        HotelDto hotelDto = modelMapper.map(hotel, HotelDto.class);

        if (hotel.getRooms() != null) {
            hotelDto.setRooms(hotel.getRooms().stream()
                    .map(e -> modelMapper.map(e, RoomDto.class)).collect(Collectors.toList()));
        } else {
            hotelDto.setRooms(new ArrayList<>());
        }
        return hotelDto;
    }

    private Hotel convertToEntity(HotelDto hotelDto) throws ParseException {
        return modelMapper.map(hotelDto, Hotel.class);
    }

    private RoomDto convertToDto(Room room) throws ParseException {
        RoomDto roomDto = modelMapper.map(room, RoomDto.class);
        roomDto.setHotelId(room.getHotel().getId());
        return roomDto;
    }
}


