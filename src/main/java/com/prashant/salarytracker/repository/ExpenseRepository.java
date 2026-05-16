package com.prashant.salarytracker.repository;

import java.time.LocalDate;
import com.prashant.salarytracker.model.Expense;
import com.prashant.salarytracker.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    List<Expense> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title
    );

    List<Expense> findByUserAndCategoryIgnoreCase(
            User user,
            String category
    );

    List<Expense> findByUserAndExpenseDateBetween(
        User user,
        LocalDate startDate,
        LocalDate endDate
);
long count();
}