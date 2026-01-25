package com.example.Horacije.administration.repository;

import com.example.Horacije.administration.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends
        JpaRepository<Bill,Integer>,
        JpaSpecificationExecutor<Bill> {
    List<Bill> findAllByArticleName(String articleName);

    List<Bill> findAllByArticleType(String articleType);

    Optional<Bill> findByBillDate(LocalDate billDate);

    List<Bill> findAllByBillDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT SUM(b.articlePrice) FROM Bill b " +
            "WHERE b.billDate BETWEEN :startDate AND :endDate")
    BigDecimal sumArticlePriceByBillDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT SUM(b.articlePrice) FROM Bill b " + "WHERE b.billDate BETWEEN :startDate AND :endDate " + "AND b.articleName = :articleName")
    BigDecimal sumByArticleNameAndBillDateBetween(String articleName, LocalDate startDate, LocalDate endDate);
    @Query("SELECT SUM(b.articlePrice) FROM Bill b " + "WHERE b.billDate BETWEEN :startDate AND :endDate " + "AND b.articleType = :articleType")
    BigDecimal sumByArticleTypeAndBillDateBetween(String articleType, LocalDate startDate, LocalDate endDate);



}
