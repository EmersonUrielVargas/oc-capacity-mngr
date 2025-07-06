package com.onclass.capacity.domain.utilities;

import com.onclass.capacity.domain.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPage<T> {
    private List<T> data;
    private Long totalItems;
    private Integer totalPages;
    private Boolean isLastPage;
    private Integer currentPage;
    private Integer pageSize;

    public <S> CustomPage(List<T> newData, CustomPage<S> sourcePage) {
        this.data = newData;
        if (Objects.nonNull(sourcePage)){
            this.totalItems = sourcePage.getTotalItems();
            this.totalPages = sourcePage.getTotalPages();
            this.isLastPage = sourcePage.getIsLastPage();
            this.currentPage = sourcePage.getCurrentPage();
            this.pageSize = sourcePage.getPageSize();
        }
    }

    public static <T> CustomPage<T> buildCustomPage(List<T> data, Integer currentPage, Integer pageSize, Long totalItems){
        Integer totalPages = totalItems < pageSize? Constants.MIN_TOTAL_PAGE : Math.toIntExact(totalItems / pageSize);
        Boolean isLastPage = data.size() < pageSize || (long) currentPage * pageSize == totalItems;
        return new CustomPage<>(data, totalItems, totalPages, isLastPage, currentPage, pageSize);
    }
}
