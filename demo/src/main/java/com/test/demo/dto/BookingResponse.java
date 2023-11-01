package com.test.demo.dto;

import com.test.demo.model.Booking;
import com.test.demo.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class BookingResponse {
    private List<Booking> content;
    private int pageNo;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
    private boolean last;
}
