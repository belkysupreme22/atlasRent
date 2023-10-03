package com.test.demo.dto;

import com.test.demo.model.Product;
import com.test.demo.model.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private List<UserEntity> content;
    private int pageNo;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
    private boolean last;
}
