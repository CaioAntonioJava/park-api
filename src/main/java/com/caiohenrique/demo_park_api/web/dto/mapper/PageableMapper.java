package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.web.dto.PageableDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableMapper {

    public static <T> PageableDTO<T> toPageableDto(Page<T> page) {

        PageableDTO<T> dto = new PageableDTO<>();

        dto.setContent(page.getContent());
        dto.setFirst(page.isFirst());
        dto.setLast(page.isLast());
        dto.setNumber(page.getNumber());
        dto.setSize(page.getSize());
        dto.setNumberOfElements(page.getNumberOfElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setTotalElements((int) page.getTotalElements());

        return dto;
    }
}
