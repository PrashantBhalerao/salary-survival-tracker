package com.prashant.salarytracker.service;

import com.prashant.salarytracker.model.Expense;
import com.prashant.salarytracker.model.User;
import com.prashant.salarytracker.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

        @Autowired
        private ExpenseRepository expenseRepository;

        // SAVE EXPENSE
        public void saveExpense(Expense expense) {

                expenseRepository.save(expense);
        }

        // GET USER EXPENSES
        public List<Expense> getUserExpenses(User user) {

                return expenseRepository.findByUser(user);
        }

        // TOTAL EXPENSES
        public double getTotalExpenses(User user) {

                List<Expense> expenses = expenseRepository.findByUser(user);

                double total = 0;

                for (Expense expense : expenses) {

                        total += expense.getAmount();
                }

                return total;
        }

        // TOTAL NUMBER OF EXPENSES
        public int getTotalExpenseCount(User user) {

                return expenseRepository.findByUser(user).size();
        }

        // HIGHEST EXPENSE
        public double getHighestExpense(User user) {

                List<Expense> expenses = expenseRepository.findByUser(user);

                double highest = 0;

                for (Expense expense : expenses) {

                        if (expense.getAmount() > highest) {

                                highest = expense.getAmount();
                        }
                }

                return highest;
        }

        // LOWEST EXPENSE
        public double getLowestExpense(User user) {

                List<Expense> expenses = expenseRepository.findByUser(user);

                if (expenses.isEmpty()) {

                        return 0;
                }

                double lowest = expenses.get(0).getAmount();

                for (Expense expense : expenses) {

                        if (expense.getAmount() < lowest) {

                                lowest = expense.getAmount();
                        }
                }

                return lowest;
        }

        // AVERAGE EXPENSE
        public double getAverageExpense(User user) {

                List<Expense> expenses = expenseRepository.findByUser(user);

                if (expenses.isEmpty()) {

                        return 0;
                }

                double total = getTotalExpenses(user);

                return Math.round(
                                (total / expenses.size()) * 100.0) / 100.0;
        }

        // CATEGORY TOTAL
        public double getCategoryTotal(
                        User user,
                        String category) {

                List<Expense> expenses = expenseRepository.findByUser(user);

                double total = 0;

                for (Expense expense : expenses) {

                        if (expense.getCategory()
                                        .equalsIgnoreCase(category)) {

                                total += expense.getAmount();
                        }
                }

                return total;
        }

        // SEARCH EXPENSES
        public List<Expense> searchExpenses(
                        User user,
                        String keyword) {

                return expenseRepository
                                .findByUserAndTitleContainingIgnoreCase(
                                                user,
                                                keyword);
        }

        // FILTER BY CATEGORY
        public List<Expense> filterByCategory(
                        User user,
                        String category) {

                return expenseRepository
                                .findByUserAndCategoryIgnoreCase(
                                                user,
                                                category);
        }

        // MONTHLY EXPENSES

        public double getMonthlyExpenses(User user) {

                LocalDate today = LocalDate.now();

                LocalDate startDate = today.withDayOfMonth(1);

                LocalDate endDate = today.withDayOfMonth(
                                today.lengthOfMonth());

                List<Expense> monthlyExpenses = expenseRepository
                                .findByUserAndExpenseDateBetween(
                                                user,
                                                startDate,
                                                endDate);

                double total = 0;

                for (Expense expense : monthlyExpenses) {

                        total += expense.getAmount();
                }

                return total;
        }

        // GET EXPENSE BY ID

        public Optional<Expense> getExpenseById(
                        Long id) {

                return expenseRepository.findById(id);
        }
        
        // DELETE EXPENSE

public void deleteExpense(
        Long id
) {

    expenseRepository.deleteById(id);
}

// TOTAL PLATFORM EXPENSES

public double getTotalPlatformExpenses() {

    List<Expense> expenses =
            expenseRepository.findAll();

    double total = 0;

    for(Expense expense : expenses) {

        total += expense.getAmount();
    }

    return total;
}

// TOTAL EXPENSE ENTRIES

public long getAllExpensesCount() {

    return expenseRepository.count();
}

}