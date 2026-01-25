package com.example.Horacije.administration.specification;

import com.example.Horacije.administration.model.Bill;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BillSpecification {

    /**
     * Creates a case-insensitive substring filter for articleName.
     *
     * @param name the text to search for inside articleName (nullable)
     * @return a Specification filtering by articleName, or null if name is empty
     */
    public static Specification<Bill> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("articleName")),"%"+name.toLowerCase()+"%");
    }

    /**
     * Creates a case-insensitive substring filter for articleType.
     *
     * @param type the article type to match (nullable)
     * @return a Specification filtering by articleType, or null if type is empty
     */
    public static Specification<Bill> hasType(String type) {
        return (root, query, cb) ->
                type == null || type.isBlank()
        ? null :
                cb.like(cb.lower(root.get("articleType")),"%"+type.toLowerCase()+"%");
    }

    /**
     * Creates a case-insensitive substring filter for brandName.
     *
     * @param brand the brand text to search for (nullable)
     * @return a Specification filtering by brandName, or null if brand is empty
     */
    public static Specification<Bill> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null || brand.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("brandName")),"%"+brand.toLowerCase()+"%");
    }

    /**
     * Filters bills with articlePrice >= minPrice.
     *
     * @param minPrice the minimum price (nullable)
     * @return a Specification applying a >= filter, or null if minPrice is not provided
     */
    public static Specification<Bill> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? null
                        : cb.greaterThan(root.get("articlePrice"), minPrice);
    }

    /**
     * Filters bills with articlePrice <= maxPrice
     *
     * @param maxPrice the maximum price (nullable)
     * @return a Specification applying a <= filter, or null if maxPrice is not provided
     */
    public static Specification<Bill> priceLessThan(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? null
                        : cb.lessThan(root.get("articlePrice"), maxPrice);
    }

    /**
     * Filters bills with billDate >= dateFrom.
     *
     * @param dateFrom the start date (nullable)
     * @return a Specification applying a >= date filter, or null if dateFrom is not provided
     */
    public static Specification<Bill> dateAfter(LocalDate dateFrom) {
        return (root, query, cb) ->
                dateFrom == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("billDate"), dateFrom);
    }

    /**
     * Filters bills with billDate <= dateTo.
     *
     * @param dateTo the end date (nullable)
     * @return a Specification applying a <= date filter, or null if dateTo is not provided
     */
    public static Specification<Bill> dateBefore(LocalDate dateTo) {
        return (root, query, cb) ->
                dateTo == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("billDate"), dateTo);
    }
}
