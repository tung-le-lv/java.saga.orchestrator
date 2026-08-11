package com.openmind.shared.application.behaviors;

import com.openmind.shared.application.commands.Command;
import com.openmind.shared.application.commands.CommandHandlerDelegate;
import com.openmind.shared.application.commands.CommandPipelineBehavior;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Pipeline behavior that logs command execution and outcome.
 */
@Component
@Order(1)
public class LoggingBehavior implements CommandPipelineBehavior {

    private static final Logger log = LoggerFactory.getLogger(LoggingBehavior.class);

    @Override
    public <TResult> CommandResult<TResult> handle(Command<TResult> command, CommandHandlerDelegate<TResult> next) {
        String commandName = command.getClass().getSimpleName();
        log.info("[{}] Handling command", commandName);

        CommandResult<TResult> result = next.next();

        if (result.isSuccess()) {
            log.info("[{}] Handled successfully", commandName);
        } else {
            log.warn("[{}] Failed: {} ({})", commandName, result.getErrorMessage(), result.getErrorCode());
        }

        return result;
    }
}
