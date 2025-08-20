package com.daisy.tickets.mappers;


import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.domain.CreateTicketTypeRequest;
import com.daisy.tickets.domain.UpdateEventRequest;
import com.daisy.tickets.domain.UpdateTicketTypeRequest;
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

    UpdateTicketTypeRequest fromDTO(UpdateTicketTypeRequestDTO dto);

    UpdateEventRequest fromDTO(UpdateEventRequestDTO dto);

    UpdateTicketTypeResponseDTO toUpdateTicketTypeDTO(TicketType ticketType);

    UpdateEventResponseDTO toUpdateEventResponseDTO(Event event);

    ListPublishedEventResponseDTO toListPublishedEventResponseDTO(Event event);

    GetPublishedEventTicketTypesResponseDTO toGetPublishedEventTicketTypesResponseDTO(TicketType ticketType);

    GetPublishedEventDetailsResponseDTO toGetPublishedEventDetailsResponseDTO(Event event);

}
