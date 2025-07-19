package com.daisy.tickets.services;


import com.daisy.tickets.domain.Event;
import com.daisy.tickets.domain.dtos.CreateEventRequest;

import java.util.UUID;

public interface EventService {
    Event creteEvent(UUID organizerId, CreateEventRequest request);
}
