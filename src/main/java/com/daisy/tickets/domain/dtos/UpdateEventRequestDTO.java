package com.daisy.tickets.domain.dtos;

import com.daisy.tickets.domain.entities.EventStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateEventRequestDTO {

    @NotNull(message = "Event ID must be provided.")
    private UUID id;

    @NotBlank(message = "Event name is required.")
    private String name;

    private LocalDateTime start;
    private LocalDateTime end;

    @NotBlank(message = "Venue of the event is required.")
    private String venue;

    private LocalDateTime salesStart;
    private LocalDateTime salesEnd;

    @NotNull(message = "Event status must be provided.")
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required.")
    @Valid
    private List<UpdateTicketTypeRequestDTO> ticketTypes = new ArrayList<>();

}
