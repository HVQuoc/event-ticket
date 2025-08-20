package com.daisy.tickets.controllers;

import com.daisy.tickets.domain.dtos.GetPublishedEventDetailsResponseDTO;
import com.daisy.tickets.domain.dtos.ListPublishedEventResponseDTO;
import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.mappers.EventMapper;
import com.daisy.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDTO>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable)
    {

        Page<Event> events;
        if (q != null && !q.trim().isEmpty()) {
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(events.map(eventMapper::toListPublishedEventResponseDTO));
    }

    @GetMapping("{eventId}")
    public ResponseEntity<GetPublishedEventDetailsResponseDTO> getPublishedEvent(
            @PathVariable UUID eventId
    ) {
         return eventService.getPublishedEvent(eventId)
                .map(eventMapper::toGetPublishedEventDetailsResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
