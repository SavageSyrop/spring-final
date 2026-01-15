package own.savage.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import own.savage.dto.RoomDto;
import own.savage.dto.RoomReservationDto;
import own.savage.entities.Room;
import own.savage.entities.RoomReservationLock;
import own.savage.service.HotelService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private final HotelService hotelService;

    @Autowired
    private ModelMapper modelMapper;

    public RoomController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public RoomDto createRoom(@RequestBody RoomDto roomDto) {
        Room room = convertToEntity(roomDto);
        return convertToDto(hotelService.saveRoom(room));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<RoomDto> updateRoom(@RequestParam Long id, @RequestBody RoomDto roomDto) {
        if (hotelService.getRoomById(id).isPresent()) {
            return ResponseEntity.ok(convertToDto(hotelService.saveRoom(convertToEntity(roomDto))));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        hotelService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/hold")
    public ResponseEntity<RoomReservationLock> hold(@RequestBody RoomReservationDto roomReservationDto) {
        try {
            RoomReservationLock lock = hotelService.holdRoom(roomReservationDto.getId(), roomReservationDto.getStartDate(), roomReservationDto.getEndDate());
            return ResponseEntity.ok(lock);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/release")
    public ResponseEntity<RoomReservationLock> release(@RequestParam Long lockId) {
        try {
            return ResponseEntity.ok(hotelService.releaseHold(lockId));
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private RoomDto convertToDto(Room room) throws ParseException {
        RoomDto roomDto = modelMapper.map(room, RoomDto.class);
        roomDto.setHotelId(room.getHotelId());
        return roomDto;
    }

    private Room convertToEntity(RoomDto roomDto) throws ParseException {
        return modelMapper.map(roomDto, Room.class);
    }
}


