package com.daisy.tickets.domain.dtos;


import com.daisy.tickets.domain.entities.TicketValidationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequestDTO {
    private UUID id;
    private TicketValidationMethod method;

}
