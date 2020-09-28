package com.github.upatovav.spp.testtask.dto;

import com.github.upatovav.spp.testtask.ProductDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    @Length(min = 9, max = 9)
    @NotNull
    private String seller;
    @NotNull
    @Length(min = 9, max = 9)
    private String customer;
    @Singular
    @NotEmpty
    @Valid
    List<ProductDto> products;
}
