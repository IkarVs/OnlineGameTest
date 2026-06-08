package com.onlinegame.technique;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TechniqueRepository extends JpaRepository<Technique, Long> {
    boolean existsByName(String name);
}
