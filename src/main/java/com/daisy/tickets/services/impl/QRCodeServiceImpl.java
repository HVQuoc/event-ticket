package com.daisy.tickets.services.impl;


import com.daisy.tickets.domain.entities.QrCode;
import com.daisy.tickets.domain.entities.Ticket;
import com.daisy.tickets.services.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public QrCode generateQRCode(Ticket ticket) {
        return null;
    }
}
