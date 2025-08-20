package com.daisy.tickets.repositories;

import com.daisy.tickets.domain.entities.QrCode;
import com.daisy.tickets.domain.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QRCodeRepository extends JpaRepository<QrCode, UUID> {
}
