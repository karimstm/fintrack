package com.fintrack.adapter.out.persistence.adapter;

import com.fintrack.adapter.out.persistence.mapper.BudgetPersistenceMapper;
import com.fintrack.adapter.out.persistence.repository.BudgetJpaRepository;
import com.fintrack.application.port.out.budget.LoadBudgetPort;
import com.fintrack.application.port.out.budget.SaveBudgetPort;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class BudgetPersistenceAdapter
        implements LoadBudgetPort, SaveBudgetPort {

    private final BudgetJpaRepository repository;
    private final BudgetPersistenceMapper mapper;

    public BudgetPersistenceAdapter(
            BudgetJpaRepository repository,
            BudgetPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Budget> loadById(BudgetId budgetId) {
        return repository.findById(budgetId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Budget> loadByOwnerCategoryAndPeriod(
            UserId ownerId, Category category, BudgetPeriod period) {
        return repository.findByOwnerIdAndCategoryAndPeriodYearAndPeriodMonth(
                        ownerId.value(),
                        category.name(),
                        period.yearMonth().getYear(),
                        period.yearMonth().getMonthValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Budget> loadAllByOwnerAndPeriod(UserId ownerId, BudgetPeriod period) {
        return repository.findAllByOwnerIdAndPeriodYearAndPeriodMonth(
                        ownerId.value(),
                        period.yearMonth().getYear(),
                        period.yearMonth().getMonthValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Budget> loadAllByOwner(UserId ownerId) {
        return repository.findAllByOwnerId(ownerId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Budget save(Budget budget) {
        var entity = mapper.toJpaEntity(budget);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}