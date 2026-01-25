package com.example.Horacije.administration.controller;

import com.example.Horacije.administration.model.Bill;
import com.example.Horacije.administration.services.BillServiceCrud;

import com.example.Horacije.administration.services.BillServiceFunctions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
@CrossOrigin(origins = "http://localhost:3000")
public class BillController {

    private final BillServiceCrud billServiceCrud;
    private final BillServiceFunctions billServiceFunctions;

    @PostMapping("/create")
    public ResponseEntity<String> createBill(
            @RequestBody
            @NotNull(message = "Models can not be null")
            List<Bill> models) {
        return billServiceCrud.createBill(models);
    }

    @GetMapping("/read-all")
    public ResponseEntity<List<Bill>> readAllBills() {
        return billServiceCrud.readAllBills();
    }

    @GetMapping("/read-by-id/{id}")
    public ResponseEntity<Bill> readBillById(
            @PathVariable("id")
            @NotNull(message = "id can not be null")
            @Min(value = 1, message = "id can not be less then zero")
            Integer id) {
        return billServiceCrud.readBillById(id);
    }

    @GetMapping("/read-by-article-name")
    public ResponseEntity<List<Bill>> readAllBillsByArticleName(
            @RequestParam("article_name")
            @NotEmpty(message = "article name can not be empty or null")
            String articleName) {
        return billServiceCrud.readAllBillsByArticleName(articleName);
    }

    @GetMapping("/read-by-article-type")
    public ResponseEntity<List<Bill>> readAllBillsByArticleType(
            @RequestParam("article_type")
            @NotEmpty(message = "article type can not be empty or null")
            String articleType) {
        return billServiceCrud.readAllBillsByArticleType(articleType);
    }

    @GetMapping("/read-by-bill-date")
    public ResponseEntity<Bill> readBillByBillDate(
            @RequestParam("bill_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate billDate
    ) {
        return billServiceCrud.readByBillDate(billDate);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateBill(
            @PathVariable("id")
            @NotNull(message = "id can not be null")
            @Min(value = 1, message = "id can not be less then zero")
            Integer id,
            @Valid
            @RequestBody
            @NotNull(message = "bill can not be null")
            Bill model) {
        return billServiceCrud.update(id, model);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBill(
            @PathVariable("id")
            @NotNull(message = "id can not be null")
            @Min(value = 1, message = "id can not be less then zero")
            Integer id
    ) {
        return billServiceCrud.deleteBill(id);
    }

    @GetMapping("read-by-date-range")
    public ResponseEntity<List<Bill>> readBillsByDateRange(
            @RequestParam("start_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @RequestParam("end_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate
    ) {
        return billServiceFunctions.findAllByBillDateBetween(startDate, endDate);
    }

    @GetMapping("/sum-by-date-range")
    public ResponseEntity<BigDecimal> sumArticlePriceByDateRange(
            @RequestParam("start_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,

            @RequestParam("end_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate
    ) {
        return billServiceFunctions.sumArticlePriceByDateRange(startDate, endDate);
    }

    @GetMapping("/sum-by-article-name")
    public ResponseEntity<BigDecimal> sumByArticleNameAndDateRange(
            @RequestParam("start_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,

            @RequestParam("end_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate,

            @RequestParam("article_name")
            @NotEmpty(message = "Article name must not be empty")
            String articleName
    ) {
        return billServiceFunctions.sumArticlePriceByArticleName(articleName, startDate, endDate);
    }

    @GetMapping("/sum-by-article-type")
    public ResponseEntity<BigDecimal> sumByArticleTypeAndDateRange(
            @RequestParam("start_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,

            @RequestParam("end_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate,

            @RequestParam("article_type")
            @NotEmpty(message = "Article type must not be empty")
            String articleType
    ) {
        return billServiceFunctions.sumByArticleTypeAndDateRange(articleType, startDate, endDate);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Bill>> searchBills(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "billDate")
            String sortBy, @RequestParam(defaultValue = "desc")
            String direction) {
        return ResponseEntity.ok(
                billServiceFunctions.searchBills(
                        name, type, brand, priceMin,
                        priceMax, dateFrom, dateTo,
                        page, size, sortBy, direction));
    }
    @GetMapping("/sum")
    public ResponseEntity<BigDecimal> sumFilteredBills(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo
    ){
        return billServiceFunctions.sumFilteredBills(
                name, type, brand,
                priceMin, priceMax,
                dateFrom, dateTo);
    }
}
