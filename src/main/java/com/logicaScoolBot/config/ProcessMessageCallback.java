package com.logicaScoolBot.config;

import com.logicaScoolBot.dto.kafka.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Callback для обработки результатов отправки сообщения в топик.
 */
@Slf4j
public class ProcessMessageCallback implements ListenableFutureCallback<SendResult<String, KafkaEvent>> {

    private final KafkaEvent message;

    public ProcessMessageCallback(KafkaEvent message) {
        this.message = message;
    }

    @Override
    public void onFailure(@NonNull Throwable ex) {
        throw new KafkaException(String.format("Невозможно отправить сообщение \"%s\"", message), ex);
    }

    @Override
    public void onSuccess(SendResult<String, KafkaEvent> result) {
        if (result != null) {
            log.info("Сообщение \"{}\" отправлено, offset = {}", message, result.getRecordMetadata().offset());
        } else {
            log.info("Сообщение \"{}\" отправлено, result = null", message);
        }
    }
}
