package com.daisy.tickets.services;


import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.domain.CreateEventRequest;

import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest request);
}
