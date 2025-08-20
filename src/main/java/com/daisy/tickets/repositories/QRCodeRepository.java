package com.daisy.tickets.repositories;

import com.daisy.tickets.domain.entities.QrCode;
import com.daisy.tickets.domain.entities.Ticket;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCodeRepository {

    QrCode generateQRCode(Ticket ticket);

}
