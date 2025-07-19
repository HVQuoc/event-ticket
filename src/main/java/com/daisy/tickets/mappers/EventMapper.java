package com.daisy.tickets.mappers;


import com.daisy.tickets.domain.CreateEventRequest;
import com.daisy.tickets.domain.CreateTicketTypeRequest;
import com.daisy.tickets.domain.dtos.CreateEventRequestDTO;
import com.daisy.tickets.domain.dtos.CreateEventResponseDTO;
import com.daisy.tickets.domain.dtos.CreateTicketTypeRequestDTO;
import com.daisy.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDTO(CreateTicketTypeRequestDTO dto);

    CreateEventRequest fromDTO(CreateEventRequestDTO dto);

    CreateEventResponseDTO toDTO(Event event);
}
