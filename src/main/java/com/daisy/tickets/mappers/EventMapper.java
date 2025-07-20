package com.daisy.tickets.mappers;


import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.domain.CreateTicketTypeRequest;
import com.daisy.tickets.domain.dtos.*;
import com.daisy.tickets.domain.entities.Event;
import com.daisy.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDTO(CreateTicketTypeRequestDTO dto);

    CreateEventRequest fromDTO(CreateEventRequestDTO dto);

    CreateEventResponseDTO toDTO(Event event);

    ListEventTicketTypeResponseDTO toDTO(TicketType ticketType);

    ListEventResponseDTO toListEventResponseDTO(Event event);

    GetEventTicketTypesResponseDTO toGetEventTicketTypesResponseDTO(TicketType ticketType);

    GetEventDetailsResponseDTO toEventDetailsResponseDTO(Event event);

}
