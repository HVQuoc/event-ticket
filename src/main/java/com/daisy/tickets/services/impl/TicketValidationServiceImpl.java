package com.daisy.tickets.services.impl;


import com.daisy.tickets.domain.entities.*;
import com.daisy.tickets.exceptions.QRCodeNotFoundException;
import com.daisy.tickets.exceptions.TicketNotFoundException;
import com.daisy.tickets.repositories.QRCodeRepository;
import com.daisy.tickets.repositories.TicketRepository;
import com.daisy.tickets.repositories.TicketValidationRepository;
import com.daisy.tickets.services.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {

    private final TicketValidationRepository ticketValidationRepository;
    private final QRCodeRepository qrCodeRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketValidation validateTicketByQRCode(UUID qrCodeId) {

        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QRCodeNotFoundException(
                        String.format(
                                "QR Code  with id %s was not found",
                                qrCodeId
                        )
                ));

        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket);
    }

    private TicketValidation validateTicket(Ticket ticket) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(TicketValidationMethod.QR_SCAN);

        TicketValidationStatusEnum status = ticket.getValidations()
                .stream()
                .filter(v -> TicketValidationStatusEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v -> TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);

        ticketValidation.setStatus(status);
        return ticketValidationRepository.save(ticketValidation);
    }

    @Override
    @Transactional
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket);
    }
}
