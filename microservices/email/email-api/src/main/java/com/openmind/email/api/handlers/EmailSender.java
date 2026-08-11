package com.openmind.email.api.handlers;

import com.openmind.email.contract.events.EmailFailedEvent;
import com.openmind.email.contract.events.EmailSentEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a transactional email provider (SendGrid/SES/...): logs the "send" and publishes
 * the outcome. There's no real email provider here; the outcome is randomized (~95% delivered)
 * purely to exercise the saga's EmailFailed branches too.
 */
@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final MessagePublisher messagePublisher;

    public EmailSender(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public void send(UUID correlationId, UUID orderId, String emailType, String toAddress) {
        if (ThreadLocalRandom.current().nextInt(100) < 95) {
            log.info("[Email] Sent {} to {} - OrderId: {}", emailType, toAddress, orderId);
            messagePublisher.publish(Topics.EMAIL_EVENTS, new EmailSentEvent(correlationId, orderId, emailType));
        } else {
            log.warn("[Email] Failed to send {} to {} - OrderId: {}", emailType, toAddress, orderId);
            messagePublisher.publish(Topics.EMAIL_EVENTS, new EmailFailedEvent(correlationId, orderId, "Email provider unavailable"));
        }
    }
}
