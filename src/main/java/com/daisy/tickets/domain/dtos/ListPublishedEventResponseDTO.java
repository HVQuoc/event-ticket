package com.daisy.tickets.domain.dtos;


import com.daisy.tickets.domain.entities.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListPublishedEventResponseDTO {

    private UUID id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;

}
