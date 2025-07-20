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


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDTO {

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
    @Valid // activate any validations in CreateTicketTypeRequestDTO class
    private List<CreateTicketTypeRequestDTO> ticketTypes = new ArrayList<>();

}
