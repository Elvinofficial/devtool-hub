
package com.devtoolshub.service;

import com.devtoolshub.entity.Developer;
import com.devtoolshub.repository.DeveloperRepository;
import com.devtoolshub.util.NameNormalizer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class DeveloperService {

    private final DeveloperRepository repository;

    public DeveloperService(DeveloperRepository repository) {
        this.repository = repository;
    }

    // Возвращает список всех разработчиков
    @Transactional(readOnly = true)
    public List<Developer> findAll() {
        return repository.findAll();
    }

    // Создаёт нового разработчика
    @Transactional
    public Developer create(
            String firstName,
            String middleName,
            String lastName,
            String email,
            String specialization
    ) {
        String normalizedFirstName =
                NameNormalizer.normalize(firstName, 100);

        String normalizedEmail = normalizeEmail(email);

        validateUniqueEmail(normalizedEmail, null);

        Developer developer = new Developer(
                normalizedFirstName,
                normalizedEmail
        );

        developer.setMiddleName(
                normalizeOptional(middleName, 100)
        );

        developer.setLastName(
                normalizeOptional(lastName, 100)
        );

        developer.setSpecialization(
                normalizeOptional(specialization, 150)
        );

        return repository.save(developer);
    }

    // Обновляет данные разработчика
    @Transactional
    public Developer update(
            Long id,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String specialization
    ) {
        Developer developer = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Разработчик не найден"
                ));

        String normalizedFirstName =
                NameNormalizer.normalize(firstName, 100);

        String normalizedEmail = normalizeEmail(email);

        validateUniqueEmail(normalizedEmail, id);

        developer.setFirstName(normalizedFirstName);
        developer.setMiddleName(
                normalizeOptional(middleName, 100)
        );
        developer.setLastName(
                normalizeOptional(lastName, 100)
        );
        developer.setEmail(normalizedEmail);
        developer.setSpecialization(
                normalizeOptional(specialization, 150)
        );

        return repository.save(developer);
    }

    // Удаляет разработчика по идентификатору
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Разработчик не найден"
            );
        }

        repository.deleteById(id);
    }

    // Проверяет уникальность электронной почты
    private void validateUniqueEmail(String email, Long currentId) {
        repository.findByEmail(email)
                .filter(existing ->
                        currentId == null
                                || !existing.getId().equals(currentId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Разработчик с таким email уже существует"
                    );
                });
    }

    // Проверяет и нормализует электронную почту
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email не может быть пустым"
            );
        }

        String normalized = email.trim()
                .toLowerCase(Locale.ROOT);

        if (normalized.length() > 255) {
            throw new IllegalArgumentException(
                    "Email не должен превышать 255 символов"
            );
        }

        if (!normalized.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
        )) {
            throw new IllegalArgumentException(
                    "Некорректный формат email"
            );
        }

        return normalized;
    }

    // Нормализует необязательное текстовое поле
    private String normalizeOptional(String value, int maxLength) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Длина поля не должна превышать "
                            + maxLength + " символов"
            );
        }

        return normalized;
    }
}
