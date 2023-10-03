package com.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ProductDto {
    private String name;
    private double price;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String category;
}
