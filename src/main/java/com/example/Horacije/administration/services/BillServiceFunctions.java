package com.example.Horacije.administration.services;

import com.example.Horacije.administration.model.Bill;
import com.example.Horacije.administration.repository.BillRepository;
import com.example.Horacije.administration.specification.BillSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BillServiceFunctions {

    private final BillRepository billRepository;



    /**
     * Retrieves all bills within the specified date range.
     *
     * @param startDate lower bound of the date range (inclusive)
     * @param endDate   upper bound of the date range (inclusive)
     * @return 200 OK with the list of bills, or 204 No Content if none found
     */
    public ResponseEntity<List<Bill>> findAllByBillDateBetween(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = billRepository.findAllByBillDateBetween(startDate, endDate);
        return bills.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(bills);
    }

    /**
     * Calculates the total article price for the given date range.
     *
     * @param startDate lower bound of the date range (inclusive)
     * @param endDate   upper bound of the date range (inclusive)
     * @return 200 OK with the total sum (0 if no records found)
     */
    public ResponseEntity<BigDecimal> sumArticlePriceByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = billRepository.sumArticlePriceByBillDateBetween(startDate, endDate);
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }
        return ResponseEntity.ok(sum);

    }

    /**
     * Returns the total article price for the given article name
     * within the specified date range.
     *
     * @param startDate   lower bound of the date range (inclusive)
     * @param endDate     upper bound of the date range (inclusive)
     * @param articleName name of the article to filter by
     * @return total sum of article prices, or null if no records match
     */
    public ResponseEntity<BigDecimal> sumArticlePriceByArticleName(String articleName, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = billRepository.sumByArticleNameAndBillDateBetween(articleName, startDate, endDate);
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }
        return ResponseEntity.ok(sum);

    }

    /**
     * Returns the total article price for the given article type
     * within the specified date range.
     *
     * @param startDate   lower bound of the date range (inclusive)
     * @param endDate     upper bound of the date range (inclusive)
     * @param articleType type of the article to filter by
     * @return total sum of article prices, or null if no records match
     */
    public ResponseEntity<BigDecimal> sumByArticleTypeAndDateRange(String articleType, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = billRepository.sumByArticleTypeAndBillDateBetween(articleType, startDate, endDate);

        if (sum == null) {
            sum = BigDecimal.ZERO;
        }

        return ResponseEntity.ok(sum);
    }

    /**
     * Searches bills using dynamic filters, pagination and sorting.
     *
     * @param name      article name filter (nullable)
     * @param type      article type filter (nullable)
     * @param brand     brand name filter (nullable)
     * @param priceMin  minimum price (nullable)
     * @param priceMax  maximum price (nullable)
     * @param dateFrom  start date (nullable)
     * @param dateTo    end date (nullable)
     * @param page      page number (0-based)
     * @param size      page size
     * @param sortBy    field to sort by (e.g. "billDate", "articlePrice")
     * @param direction direction sort direction ("asc" or "desc")
     * @return paginated and filtered list of bills
     */
    public Page<Bill> searchBills(String name,
                                  String type,
                                  String brand,
                                  BigDecimal priceMin,
                                  BigDecimal priceMax,
                                  LocalDate dateFrom,
                                  LocalDate dateTo,
                                  int page,
                                  int size,
                                  String sortBy,
                                  String direction) {
        // Building Specification
        Specification<Bill> specification = Specification
                .where(BillSpecification.hasName(name))
                .and(BillSpecification.hasType(type))
                .and(BillSpecification.hasBrand(brand))
                .and(BillSpecification.priceGreaterThan(priceMin))
                .and(BillSpecification.priceLessThan(priceMax))
                .and(BillSpecification.dateAfter(dateFrom))
                .and(BillSpecification.dateBefore(dateTo));

        // Check sortBy and make it default "billDate"
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "billDate";
        }

        // Check and initializes sorting direction
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();


        // Making Pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        return billRepository.findAll(specification, pageable);
    }

    /**
     * Calculates the total sum of all bills that match the provided filters.
     * Uses dynamic JPA Specifications to apply optional filters and returns
     * the aggregated price as BigDecimal.
     *
     * @param name      article name filter (nullable)
     * @param type      article type filter (nullable)
     * @param brand     brand name filter (nullable)
     * @param priceMin  minimum price filter (nullable)
     * @param priceMax  maximum price filter (nullable)
     * @param dateFrom  start date filter (nullable)
     * @param dateTo    end date filter (nullable)
     * @return          ResponseEntity containing the total sum or 204 No Content if no bills match
     */
    public ResponseEntity<BigDecimal> sumFilteredBills(String name,
                                                       String type,
                                                       String brand,
                                                       BigDecimal priceMin,
                                                       BigDecimal priceMax,
                                                       LocalDate dateFrom,
                                                       LocalDate dateTo)
    {
        Specification<Bill> specification = Specification
                .where(BillSpecification.hasName(name))
                .and(BillSpecification.hasType(type))
                .and(BillSpecification.hasBrand(brand))
                .and(BillSpecification.priceGreaterThan(priceMin))
                .and(BillSpecification.priceLessThan(priceMax))
                .and(BillSpecification.dateAfter(dateFrom))
                .and(BillSpecification.dateBefore(dateTo));

        List<Bill> bills = billRepository.findAll(specification);

        if (bills.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        //Ovako sam uradio prvi put, a onda sam napisao ovu "optimizovaniju verziju"
//        BigDecimal sum = BigDecimal.ZERO;
//
//        for (Bill bill : bills) {
//            sum = sum.add(bill.getArticlePrice());
//        }
        BigDecimal sum = bills.stream()
                .map(Bill::getArticlePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(sum);

    }
}
