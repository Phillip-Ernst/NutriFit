package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.dto.response.UserChangeHistoryResponse;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.model.entity.UserChangeHistory;
import com.phillipe.NutriFit.repository.UserChangeHistoryRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.ChangeHistoryService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ChangeHistoryServiceImpl implements ChangeHistoryService {

    private final UserChangeHistoryRepository historyRepo;
    private final UserRepository userRepo;

    public ChangeHistoryServiceImpl(UserChangeHistoryRepository historyRepo, UserRepository userRepo) {
        this.historyRepo = historyRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public void recordChange(User user, String entityType, Long entityId, String field, Object oldVal, Object newVal) {
        // Only record if values are actually different
        if (Objects.equals(oldVal, newVal)) {
            return;
        }

        UserChangeHistory history = UserChangeHistory.builder()
                .user(user)
                .entityType(entityType)
                .entityId(entityId)
                .fieldName(field)
                .oldValue(oldVal != null ? String.valueOf(oldVal) : null)
                .newValue(newVal != null ? String.valueOf(newVal) : null)
                .build();

        historyRepo.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserChangeHistoryResponse> getHistory(String username) {
        User user = findUser(username);
        return historyRepo.findByUserIdOrderByChangedAtDesc(user.getId())
                .stream()
                .map(UserChangeHistoryResponse::fromEntity)
                .toList();
    }

    private User findUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }
}
