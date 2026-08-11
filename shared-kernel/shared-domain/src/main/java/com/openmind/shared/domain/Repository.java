package com.openmind.shared.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic repository interface following the repository pattern.
 */
public interface Repository<TAggregate extends AggregateRoot> {

    Optional<TAggregate> findById(UUID id);

    List<TAggregate> findAll();

    void add(TAggregate aggregate);

    void update(TAggregate aggregate);

    void deleteById(UUID id);
}
