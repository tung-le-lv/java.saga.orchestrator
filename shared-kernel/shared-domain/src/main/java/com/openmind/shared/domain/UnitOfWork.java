package com.openmind.shared.domain;

/**
 * Unit of work interface for coordinating persistence operations, in particular
 * dispatching domain events collected on tracked aggregates.
 */
public interface UnitOfWork {

    int saveChanges();
}
