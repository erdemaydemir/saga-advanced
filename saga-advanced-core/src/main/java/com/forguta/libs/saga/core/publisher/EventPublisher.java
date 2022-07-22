package com.forguta.libs.saga.core.publisher;

import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.constant.Constant;
import com.forguta.libs.saga.core.model.constant.EventActionTypeEnum;
import com.forguta.libs.saga.core.util.EventMDCContext;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisher {

    public <T extends Event<?>> void sendAndForget(T event) {
        if (StringUtils.hasText(EventMDCContext.getCorrelationId())) {
            event.setCorrelationId(EventMDCContext.getCorrelationId());
        }
        log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.SENT, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
    }
}
