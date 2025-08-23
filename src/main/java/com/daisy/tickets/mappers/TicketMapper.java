package com.daisy.tickets.mappers;

import com.daisy.tickets.domain.dtos.ListTicketResponseDTO;
import com.daisy.tickets.domain.dtos.ListTicketResponseTicketTypeDTO;
import com.daisy.tickets.domain.entities.Ticket;
import com.daisy.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    ListTicketResponseTicketTypeDTO toListTicketResponseTicketTypeDTO(TicketType ticketType);

    ListTicketResponseDTO toListTicketResponseDTO(Ticket ticket);

}
