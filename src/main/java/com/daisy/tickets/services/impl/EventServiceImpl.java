package com.daisy.tickets.services.impl;

import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.domain.entities.TicketType;
import com.daisy.tickets.domain.entities.User;
import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.exceptions.UserNotFoundException;
import com.daisy.tickets.repositories.EventRepository;
import com.daisy.tickets.repositories.UserRepository;
import com.daisy.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest request) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + organizerId + " not found"));

        List<TicketType> ticketTypes = request.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType ttToCreate = new TicketType();
                    ttToCreate.setName(ticketType.getName());
                    ttToCreate.setPrice(ticketType.getPrice());
                    ttToCreate.setDescription(ticketType.getDescription());
                    ttToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    return ttToCreate;
                }).toList();

        Event eventToCreate = new Event();
        eventToCreate.setName(request.getName());
        eventToCreate.setStart(request.getStart());
        eventToCreate.setEnd(request.getEnd());
        eventToCreate.setVenue(request.getVenue());
        eventToCreate.setSalesStart(request.getSalesStart());
        eventToCreate.setSalesEnd(request.getSalesEnd());
        eventToCreate.setStatus(request.getStatus());
        eventToCreate.setOrganizer(organizer);

        eventToCreate.setTicketTypes(ticketTypes);

        return eventRepository.save(eventToCreate);
    }

}
