package com.example.Horacije.administration.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "bill date can not be null")
    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("bill_date")
    @Column(name = "bill_date")
    private LocalDate billDate;

    @NotBlank(message = "article name can not be empty or null")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("article_name")
    @Column(name = "article_name")
    private String articleName;

    @NotNull(message = "article price can not be null")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("article_price")
    @Column(name = "article_price", precision = 10, scale = 2)
    private BigDecimal articlePrice;

    @JsonFormat(shape =  JsonFormat.Shape.STRING)
    @JsonProperty("article_type")
    @Column(name = "article_type")
    private String articleType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("brand_name")
    @Column(name = "brand_name")
    private String brandName;

}
