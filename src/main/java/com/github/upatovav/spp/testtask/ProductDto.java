package com.github.upatovav.spp.testtask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    @NotEmpty
    private String name;
    @NotNull
    @Length(min = 13, max = 13)
    private String code;
}
