package com.daisy.tickets.services.impl;

import com.daisy.tickets.domain.UpdateEventRequest;
import com.daisy.tickets.domain.UpdateTicketTypeRequest;
import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.domain.entities.TicketType;
import com.daisy.tickets.domain.entities.User;
import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.exceptions.EventNotFoundException;
import com.daisy.tickets.exceptions.EventUpdateException;
import com.daisy.tickets.exceptions.TicketTypeNotFoundException;
import com.daisy.tickets.exceptions.UserNotFoundException;
import com.daisy.tickets.repositories.EventRepository;
import com.daisy.tickets.repositories.UserRepository;
import com.daisy.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest request) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + organizerId + " not found"));

        Event eventToCreate = new Event();

        List<TicketType> ticketTypes = request.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType ttToCreate = new TicketType();
                    ttToCreate.setName(ticketType.getName());
                    ttToCreate.setPrice(ticketType.getPrice());
                    ttToCreate.setDescription(ticketType.getDescription());
                    ttToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ttToCreate.setEvent(eventToCreate);
                    return ttToCreate;
                }).toList();

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

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID eventId) {
        return eventRepository.findByIdAndOrganizerId(eventId, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event) {
        if (event.getId() == null) {
            throw new EventUpdateException("The event id can not be null.");
        }

        if (!eventId.equals(event.getId())) {
            throw new EventUpdateException("Cannot update the ID of an event.");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " not found"));

        // Update event primitive fields
        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        // Remove the ticket types which is not existed in update request
        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypeIds.contains(existingTicketType.getId())
        );

        // Update or Create ticket type based on the list of
        // ticket type in update request
        Map<UUID, TicketType> existingTicketTypeIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if (ticketType.getId() == null) {
                // Create case
                TicketType ttToCreate = new TicketType();
                ttToCreate.setName(ticketType.getName());
                ttToCreate.setPrice(ticketType.getPrice());
                ttToCreate.setDescription(ticketType.getDescription());
                ttToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ttToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ttToCreate);
            } else if (existingTicketTypeIndex.containsKey(ticketType.getId())) {
                // Update case
                TicketType existingTicketType = existingTicketTypeIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());
            } else {
                throw new TicketTypeNotFoundException("Ticket type with id " + ticketType.getId() + " not found");
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID eventId) {
        getEventForOrganizer(organizerId, eventId).ifPresent(eventRepository::delete);
    }

}
