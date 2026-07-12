package com.microfinance.common.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long total) {
        int tp = size == 0 ? 0 : (int) Math.ceil((double) total / size);
        return PageResponse.<T>builder().content(content).page(page).size(size)
                .totalElements(total).totalPages(tp).first(page == 0).last(page >= tp - 1).build();
    }

    /** Convenience overload that accepts a Spring Data {@code Page}. */
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return of(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
