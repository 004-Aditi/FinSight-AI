package com.finsight.backend.income;

import com.finsight.backend.income.dto.*;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class IncomeService {

    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) {
        this.repo = repo;
    }

    public IncomeResponse create(IncomeRequest req) {
        User user = getUser();

        Income i = new Income();
        i.setId(UUID.randomUUID());
        i.setUser(user);
        i.setAmount(req.amount());
        i.setSource(req.source());
        i.setDescription(req.description());
        i.setIncomeDate(req.incomeDate());

        return map(repo.save(i));
    }

    public List<IncomeResponse> getAll() {
        return repo.findByUserOrderByIncomeDateDesc(getUser())
                .stream().map(this::map).toList();
    }

    public IncomeResponse getById(UUID id) {
        Income i = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        return map(i);
    }

    public IncomeResponse update(UUID id, IncomeRequest req) {
        Income i = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        i.setAmount(req.amount());
        i.setSource(req.source());
        i.setDescription(req.description());
        i.setIncomeDate(req.incomeDate());

        return map(repo.save(i));
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public double getTotal(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return repo.findByUserAndIncomeDateBetween(getUser(), start, end)
                .stream()
                .mapToDouble(i -> i.getAmount().doubleValue())
                .sum();
    }

    private IncomeResponse map(Income i) {
        return new IncomeResponse(
                i.getId(),
                i.getAmount(),
                i.getSource(),
                i.getDescription(),
                i.getIncomeDate());
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}