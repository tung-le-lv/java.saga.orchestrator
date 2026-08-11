package com.openmind.shared.mongodb;

import com.openmind.shared.application.commands.Command;
import com.openmind.shared.application.commands.CommandHandlerDelegate;
import com.openmind.shared.application.commands.CommandPipelineBehavior;
import com.openmind.shared.application.commands.CommandResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pipeline behavior that dispatches domain events after command execution.
 * This ensures events are dispatched once at the end of the unit of work, after the handler
 * (and any repository calls it made) have completed.
 */
@Component
@Order(2)
public class DomainEventDispatchBehavior implements CommandPipelineBehavior {

    private final MongoDbContext dbContext;

    public DomainEventDispatchBehavior(MongoDbContext dbContext) {
        this.dbContext = dbContext;
    }

    @Override
    public <TResult> CommandResult<TResult> handle(Command<TResult> command, CommandHandlerDelegate<TResult> next) {
        CommandResult<TResult> result = next.next();
        dbContext.saveChanges();
        return result;
    }
}
