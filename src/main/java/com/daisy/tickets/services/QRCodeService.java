package com.daisy.tickets.services;

import com.daisy.tickets.domain.entities.QrCode;
import com.daisy.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QRCodeService {

    QrCode generateQRCode(Ticket ticket);
    byte[] getQRCodeForUserAndTicket(UUID userId, UUID ticketId);

}
