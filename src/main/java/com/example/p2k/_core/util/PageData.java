package com.example.p2k._core.util;

import lombok.Getter;

@Getter
public class PageData {
    private final Boolean hasPrevious;
    private final Boolean hasNext;
    private final Boolean isEmpty;
    private final int number;
    private final int totalPages;
    private final int size;

    public PageData(Boolean hasPrevious, Boolean hasNext, Boolean isEmpty, int number, int totalPages, int size) {
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.isEmpty = isEmpty;
        this.number = number;
        this.totalPages = totalPages;
        this.size = size;
    }

    public int getStartPage() {
        if(this.getTotalPages() <= size){
            return 0;
        }
        int start = Math.max(0, this.getNumber() - size / 2);
        int max = this.getTotalPages() - size;
        return Math.min(start, max);
    }

    public int getEndPage() {
        if(this.getTotalPages() <= size){
            return getTotalPages() - 1;
        }
        int max = this.getTotalPages() - 1;
        int end = this.getStartPage() + size - 1;
        return Math.min(end, max);
    }
}
