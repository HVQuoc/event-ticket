package com.daisy.tickets.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ListTicketResponseTicketTypeDTO {
    private UUID id;
    private String name;
    private Double price;
}
