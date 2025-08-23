package com.daisy.tickets.controllers;


import com.daisy.tickets.domain.dtos.TicketValidationRequestDTO;
import com.daisy.tickets.domain.dtos.TicketValidationResponseDTO;
import com.daisy.tickets.domain.entities.TicketValidation;
import com.daisy.tickets.domain.entities.TicketValidationMethod;
import com.daisy.tickets.mappers.TicketValidationMapper;
import com.daisy.tickets.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDTO> validateTicket(
            @RequestBody final TicketValidationRequestDTO requestDTO
    ) {
        TicketValidationMethod method = requestDTO.getMethod();
        TicketValidation ticketValidation;
        if (TicketValidationMethod.MANUAL.equals(method)) {
            ticketValidation = ticketValidationService.validateTicketManually(requestDTO.getId());
        } else {
            ticketValidation = ticketValidationService.validateTicketByQRCode(requestDTO.getId());
        }

        return ResponseEntity.ok(
                ticketValidationMapper.toTicketValidationResponseDTO(ticketValidation)
        );
    }

}
