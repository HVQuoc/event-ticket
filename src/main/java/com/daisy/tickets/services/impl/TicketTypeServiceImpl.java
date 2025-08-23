package com.daisy.tickets.services.impl;

import com.daisy.tickets.domain.entities.Ticket;
import com.daisy.tickets.domain.entities.TicketStatusEnum;
import com.daisy.tickets.domain.entities.TicketType;
import com.daisy.tickets.domain.entities.User;
import com.daisy.tickets.exceptions.TicketSoldOutException;
import com.daisy.tickets.exceptions.TicketTypeNotFoundException;
import com.daisy.tickets.exceptions.UserNotFoundException;
import com.daisy.tickets.repositories.TicketRepository;
import com.daisy.tickets.repositories.TicketTypeRepository;
import com.daisy.tickets.repositories.UserRepository;
import com.daisy.tickets.services.QRCodeService;
import com.daisy.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QRCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> new TicketTypeNotFoundException(
                String.format("Ticket type with ID %s not found", ticketTypeId)
        ));

        int purchasedTicket = ticketRepository.countByTicketTypeId(ticketTypeId);
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTicket + 1 > totalAvailable) {
            throw new TicketSoldOutException("Ticket already sold out");
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQRCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }
}
