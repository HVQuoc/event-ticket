package com.daisy.tickets.services;

import com.daisy.tickets.domain.entities.QrCode;
import com.daisy.tickets.domain.entities.Ticket;

public interface QRCodeService {

    QrCode generateQRCode(Ticket ticket);

}
