package com.prashant.salarytracker.controller;

import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;
import com.prashant.salarytracker.model.Expense;
import com.prashant.salarytracker.model.User;
import com.prashant.salarytracker.service.ExpenseService;
import com.prashant.salarytracker.service.UserService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseService expenseService;

    // HOME PAGE
    @GetMapping("/")
    public String home() {

        return "index";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    // SAVE USER
    @PostMapping("/saveUser")
    public String saveUser(User user) {

        userService.saveUser(user);

        return "redirect:/login";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    // LOGIN USER
    @PostMapping("/loginUser")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // ADMIN LOGIN

        if (email.equals("admin@salarytracker.com")
                && password.equals("admin123")) {

            session.setAttribute(
                    "admin",
                    "admin");

            return "redirect:/admin";
        }
        User validUser = userService.loginUser(email, password);

        if (validUser != null) {

            session.setAttribute(
                    "loggedInUser",
                    validUser);

            return "redirect:/dashboard";
        } else {

            model.addAttribute(
                    "error",
                    "Invalid Email or Password");

            return "login";
        }
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {

            return "redirect:/login";
        }

        // TOTAL EXPENSES

        double totalExpenses = expenseService.getTotalExpenses(user);

        // REMAINING BALANCE

        double remainingBalance = Math.round(
                (user.getSalary() - totalExpenses)
                        * 100.0)
                / 100.0;

        // CURRENT DATE

        LocalDate today = LocalDate.now();

        // NEXT SALARY DATE

        LocalDate nextSalaryDate = LocalDate.of(
                today.getYear(),
                today.getMonth(),
                user.getSalaryDate());

        // IF SALARY DATE PASSED

        if (today.getDayOfMonth() > user.getSalaryDate()) {

            nextSalaryDate = nextSalaryDate.plusMonths(1);
        }

        // DAYS LEFT

        long daysLeft = ChronoUnit.DAYS.between(
                today,
                nextSalaryDate);

        if (daysLeft == 0) {

            daysLeft = 1;
        }

        // SAFE DAILY SPEND

        double safeDailySpend = Math.round(
                (remainingBalance / daysLeft)
                        * 100.0)
                / 100.0;

        // ALERT MESSAGE

        String alertMessage;

        if (safeDailySpend < 300) {

            alertMessage = "⚠️ Warning! Your balance is running low.";
        } else {

            alertMessage = "✅ You are financially safe.";
        }

        // ANALYTICS

        int totalExpenseCount = expenseService.getTotalExpenseCount(user);

        double highestExpense = expenseService.getHighestExpense(user);

        double lowestExpense = expenseService.getLowestExpense(user);

        double averageExpense = expenseService.getAverageExpense(user);

        // CATEGORY ANALYTICS

        double foodExpense = expenseService.getCategoryTotal(
                user,
                "Food");

        double travelExpense = expenseService.getCategoryTotal(
                user,
                "Travel");

        double shoppingExpense = expenseService.getCategoryTotal(
                user,
                "Shopping");

        double billsExpense = expenseService.getCategoryTotal(
                user,
                "Bills");

        double entertainmentExpense = expenseService.getCategoryTotal(
                user,
                "Entertainment");

        double otherExpense = expenseService.getCategoryTotal(
                user,
                "Other");

        // PERCENTAGE CALCULATIONS

        double usedPercentage = (totalExpenses / user.getSalary()) * 100;

        usedPercentage = Math.round(
                usedPercentage * 100.0) / 100.0;

        double remainingPercentage = 100 - usedPercentage;

        remainingPercentage = Math.round(
                remainingPercentage * 100.0) / 100.0;

        // BUDGET STATUS

        String budgetStatus;

        if (usedPercentage >= 90) {

            budgetStatus = "💀 Extreme Danger";
        } else if (usedPercentage >= 70) {

            budgetStatus = "⚠️ High Spending";
        } else if (usedPercentage >= 50) {

            budgetStatus = "😅 Moderate Spending";
        } else {

            budgetStatus = "✅ Healthy Budget";
        }

        // SAVINGS GOAL

        double savingsGoal = 0;

        if (user.getSavingsGoal() != null) {

            savingsGoal = user.getSavingsGoal();
        }

        double savingsProgress = 0;

        if (savingsGoal > 0) {

            savingsProgress = (remainingBalance / savingsGoal)
                    * 100;

            if (savingsProgress > 100) {

                savingsProgress = 100;
            }

            savingsProgress = Math.round(
                    savingsProgress * 100.0) / 100.0;
        }

        // SMART ALERT SYSTEM

        String smartAlert;

        String alertClass;

        if (remainingBalance <= 0) {

            smartAlert = "💀 You have exhausted your salary.";

            alertClass = "danger-alert";
        } else if (usedPercentage >= 90) {

            smartAlert = "🚨 Critical spending detected.";

            alertClass = "danger-alert";
        } else if (usedPercentage >= 70) {

            smartAlert = "⚠️ Control your spending habits.";

            alertClass = "warning-alert";
        } else {

            smartAlert = "✅ Your finances look healthy.";

            alertClass = "safe-alert";
        }

        // SEND DATA TO DASHBOARD

        model.addAttribute("user", user);

        model.addAttribute(
                "totalExpenses",
                totalExpenses);

        model.addAttribute(
                "remainingBalance",
                remainingBalance);

        model.addAttribute(
                "daysLeft",
                daysLeft);

        model.addAttribute(
                "safeDailySpend",
                safeDailySpend);

        model.addAttribute(
                "alertMessage",
                alertMessage);

        model.addAttribute(
                "totalExpenseCount",
                totalExpenseCount);

        model.addAttribute(
                "highestExpense",
                highestExpense);

        model.addAttribute(
                "lowestExpense",
                lowestExpense);

        model.addAttribute(
                "averageExpense",
                averageExpense);

        model.addAttribute(
                "foodExpense",
                foodExpense);

        model.addAttribute(
                "travelExpense",
                travelExpense);

        model.addAttribute(
                "shoppingExpense",
                shoppingExpense);

        model.addAttribute(
                "billsExpense",
                billsExpense);

        model.addAttribute(
                "entertainmentExpense",
                entertainmentExpense);

        model.addAttribute(
                "otherExpense",
                otherExpense);

        model.addAttribute(
                "usedPercentage",
                usedPercentage);

        model.addAttribute(
                "remainingPercentage",
                remainingPercentage);

        model.addAttribute(
                "budgetStatus",
                budgetStatus);

        model.addAttribute(
                "savingsGoal",
                savingsGoal);

        model.addAttribute(
                "savingsProgress",
                savingsProgress);

        model.addAttribute(
                "smartAlert",
                smartAlert);

        model.addAttribute(
                "alertClass",
                alertClass);

        // MONTHLY SUMMARY

        double monthlyExpenses = expenseService.getMonthlyExpenses(user);

        int monthlyExpenseCount = expenseService.getUserExpenses(user).size();

        double monthlyUsagePercentage = (monthlyExpenses / user.getSalary()) * 100;

        monthlyUsagePercentage = Math.round(
                monthlyUsagePercentage * 100.0) / 100.0;

        model.addAttribute(
                "monthlyExpenses",
                monthlyExpenses);

        model.addAttribute(
                "monthlyExpenseCount",
                monthlyExpenseCount);

        model.addAttribute(
                "monthlyUsagePercentage",
                monthlyUsagePercentage);

        // AI FINANCE TIPS

        String aiTip;

        if (foodExpense > 5000) {

            aiTip = "🍔 Your food expenses are high. Try reducing outside meals.";
        } else if (shoppingExpense > 7000) {

            aiTip = "🛍 Shopping expenses increased significantly this month.";
        } else if (travelExpense > 4000) {

            aiTip = "🚕 Travel spending is high. Consider smarter transport options.";
        } else if (remainingBalance < 2000) {

            aiTip = "⚠️ Your remaining balance is low. Spend carefully.";
        } else if (savingsProgress >= 80) {

            aiTip = "🎯 Amazing! You are very close to your savings goal.";
        } else {

            aiTip = "✅ Your financial habits look healthy this month.";
        }

        model.addAttribute(
                "aiTip",
                aiTip);

        return "dashboard";
    }

    // ADD EXPENSE PAGE
    @GetMapping("/addExpense")
    public String addExpensePage(
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        if (user == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "expense",
                new Expense());

        return "add-expense";
    }

    // EDIT EXPENSE PAGE

    @GetMapping("/editExpense/{id}")
    public String editExpensePage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        if (user == null) {

            return "redirect:/login";
        }

        Optional<Expense> expense = expenseService.getExpenseById(id);

        if (expense.isPresent()) {

            model.addAttribute(
                    "expense",
                    expense.get());

            return "edit-expense";
        }

        return "redirect:/expenses";
    }

    // SAVE EXPENSE
    @PostMapping("/saveExpense")
    public String saveExpense(
            Expense expense,
            HttpSession session) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        expense.setUser(user);

        expense.setExpenseDate(
                LocalDate.now());

        expenseService.saveExpense(expense);

        return "redirect:/expenses";
    }

    // UPDATE EXPENSE

    @PostMapping("/updateExpense")
    public String updateExpense(
            Expense expense,
            HttpSession session) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        expense.setUser(user);

        expenseService.saveExpense(expense);

        return "redirect:/expenses";
    }

    // VIEW EXPENSES
    @GetMapping("/expenses")
    public String viewExpenses(
            HttpSession session,
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        if (user == null) {

            return "redirect:/login";
        }

        List<Expense> expenses;

        // SEARCH

        if (keyword != null
                && !keyword.isEmpty()) {

            expenses = expenseService.searchExpenses(
                    user,
                    keyword);
        }

        // FILTER

        else if (category != null
                && !category.isEmpty()) {

            expenses = expenseService.filterByCategory(
                    user,
                    category);
        }

        // NORMAL

        else {

            expenses = expenseService.getUserExpenses(user);
        }

        model.addAttribute(
                "expenses",
                expenses);

        return "expenses";
    }

    // DOWNLOAD PDF REPORT
    @GetMapping("/download-pdf")
    public void downloadPdf(
            HttpSession session,
            HttpServletResponse response) throws IOException {

        User user = (User) session.getAttribute(
                "loggedInUser");

        if (user == null) {

            response.sendRedirect("/login");

            return;
        }

        List<Expense> expenses = expenseService.getUserExpenses(user);

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Expense_Report.pdf");

        try {

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    response.getOutputStream());

            document.open();

            document.add(
                    new Paragraph(
                            "Salary-to-Salary Survival Tracker"));

            document.add(
                    new Paragraph(
                            "Expense Report"));

            document.add(
                    new Paragraph(
                            "-------------------------------------"));

            for (Expense expense : expenses) {

                document.add(
                        new Paragraph(
                                "Title: "
                                        + expense.getTitle()));

                document.add(
                        new Paragraph(
                                "Amount: ₹"
                                        + expense.getAmount()));

                document.add(
                        new Paragraph(
                                "Category: "
                                        + expense.getCategory()));

                document.add(
                        new Paragraph(
                                "Date: "
                                        + expense.getExpenseDate()));

                document.add(
                        new Paragraph(
                                "-------------------------------------"));
            }

            document.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // DELETE EXPENSE

    @GetMapping("/deleteExpense/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute(
                "loggedInUser");

        if (user == null) {

            return "redirect:/login";
        }

        expenseService.deleteExpense(id);

        return "redirect:/expenses";
    }

// ADMIN DASHBOARD

@GetMapping("/admin")
public String adminDashboard(
        HttpSession session,
        Model model
) {

    String admin =
            (String) session.getAttribute(
                    "admin"
            );

    if(admin == null) {

        return "redirect:/login";
    }

    long totalUsers =
            userService.getTotalUsers();

    double totalPlatformExpenses =
            expenseService
                    .getTotalPlatformExpenses();

    long totalExpenses =
            expenseService
                    .getAllExpensesCount();

    model.addAttribute(
            "totalUsers",
            totalUsers
    );

    model.addAttribute(
            "totalPlatformExpenses",
            totalPlatformExpenses
    );

    model.addAttribute(
            "totalExpenses",
            totalExpenses
    );

    return "admin-dashboard";
}

    // LOGOUT
    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}