package com.daisy.tickets.domain.dtos;

import com.daisy.tickets.domain.entities.TicketStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTicketResponseDTO {
     private UUID id;
     private TicketStatusEnum status;
     private ListTicketResponseTicketTypeDTO ticketType;
}
