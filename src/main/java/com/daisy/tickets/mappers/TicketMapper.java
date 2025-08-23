package com.daisy.tickets.mappers;

import com.daisy.tickets.domain.dtos.GetTicketResponseDTO;
import com.daisy.tickets.domain.dtos.ListTicketResponseDTO;
import com.daisy.tickets.domain.dtos.ListTicketResponseTicketTypeDTO;
import com.daisy.tickets.domain.entities.Ticket;
import com.daisy.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    ListTicketResponseTicketTypeDTO toListTicketResponseTicketTypeDTO(TicketType ticketType);

    ListTicketResponseDTO toListTicketResponseDTO(Ticket ticket);

    @Mapping(target = "price", source = "ticket.ticketType.price")
    @Mapping(target = "description", source = "ticket.ticketType.description")
    @Mapping(target = "eventName", source = "ticket.ticketType.event.name")
    @Mapping(target = "eventVenue", source = "ticket.ticketType.event.venue")
    @Mapping(target = "eventStart", source = "ticket.ticketType.event.start")
    @Mapping(target = "eventEnd", source = "ticket.ticketType.event.end")
    GetTicketResponseDTO toGetTicketResponseDTO(Ticket ticket);

}
