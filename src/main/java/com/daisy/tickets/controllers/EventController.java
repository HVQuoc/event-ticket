package com.daisy.tickets.controllers;

import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.domain.dtos.CreateEventRequestDTO;
import com.daisy.tickets.domain.dtos.CreateEventResponseDTO;
import com.daisy.tickets.domain.dtos.ListEventResponseDTO;
import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.mappers.EventMapper;
import com.daisy.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDTO> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDTO createEventRequestDTO) {

        CreateEventRequest request = eventMapper.fromDTO(createEventRequestDTO);
        UUID userId = parseUserId(jwt);

        Event createdEvent = eventService.createEvent(userId, request);
        CreateEventResponseDTO createdEventResponseDTO = eventMapper.toDTO(createdEvent);
        return new ResponseEntity<>(createdEventResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDTO>> listEvents(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable
    ) {
        UUID userId = parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(userId, pageable);
        return ResponseEntity.ok(events.map(eventMapper::toListEventResponseDTO));
    }

    private UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

}
