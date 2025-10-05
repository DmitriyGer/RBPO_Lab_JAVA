package ru.mfa.carsharing.service;

import org.springframework.stereotype.Service;
import ru.mfa.carsharing.exception.NotFoundException;
import ru.mfa.carsharing.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<User> findAll() { return new ArrayList<>(users.values()); }

    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("User not found: " + id);
        return user;
    }

    public User create(User user) {
        Long id = seq.incrementAndGet();
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        users.put(id, user);
        return user;
    }

    public User update(Long id, User updated) {
        if (!users.containsKey(id)) throw new NotFoundException("User not found: " + id);
        updated.setId(id);
        users.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (users.remove(id) == null) throw new NotFoundException("User not found: " + id);
    }
}

