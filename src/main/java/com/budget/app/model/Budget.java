package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Budget name is required")
    @Column(nullable = false)
    private String name;

    @Positive(message = "Amount must be positive")
    @Column(nullable = false)
    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    public Budget() {}

    public Budget(String name, Double totalAmount, User user) {
        this.name        = name;
        this.totalAmount = totalAmount;
        this.user        = user;
    }

    @Transient 
    public Double getRemainingBalance() {
        if (transactions == null || transactions.isEmpty()) return totalAmount;
        double spent = transactions.stream()
            .filter(t -> t.getType() == Transaction.Type.EXPENSE)
            .mapToDouble(Transaction::getAmount)
            .sum();
        double income = transactions.stream()
            .filter(t -> t.getType() == Transaction.Type.INCOME)
            .mapToDouble(Transaction::getAmount)
            .sum();
        return totalAmount + income - spent;
    }

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public Double getTotalAmount()            { return totalAmount; }
    public void setTotalAmount(Double amount) { this.totalAmount = amount; }

    public User getUser()                     { return user; }
    public void setUser(User user)            { this.user = user; }

    public List<Transaction> getTransactions()             { return transactions; }
    public void setTransactions(List<Transaction> t)       { this.transactions = t; }

    @Override
    public String toString() {
        return "Budget{id=" + id + ", name='" + name + "', total=" + totalAmount + "}";
    }
}
