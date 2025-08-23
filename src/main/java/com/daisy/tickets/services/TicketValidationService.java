package com.daisy.tickets.services;

import com.daisy.tickets.domain.entities.TicketValidation;

import java.util.UUID;

public interface TicketValidationService {

    TicketValidation validateTicketByQRCode(UUID qrCodeId);
    TicketValidation validateTicketManually(UUID ticketId);
}
