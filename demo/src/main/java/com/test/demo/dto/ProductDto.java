package com.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductDto {
    private String name;
    private double price;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String description;
    private String category;
}
