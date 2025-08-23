package com.daisy.tickets.mappers;


import com.daisy.tickets.domain.dtos.TicketValidationResponseDTO;
import com.daisy.tickets.domain.entities.TicketValidation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketValidationMapper {

    @Mapping(target = "ticketId", source = "ticket.id")
    TicketValidationResponseDTO toTicketValidationResponseDTO(TicketValidation ticketValidation);

}
