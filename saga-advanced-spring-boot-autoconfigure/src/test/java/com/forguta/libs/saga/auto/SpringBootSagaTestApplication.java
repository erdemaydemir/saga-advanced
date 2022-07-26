package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.publisher.EventPublisher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringBootSagaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSagaTestApplication.class, args);
    }


    @RequiredArgsConstructor
    @Component
    public class SameTest{

        private final EventPublisher eventPublisher;

        @PostConstruct
        public void init(){
            eventPublisher.sendAndForget(EventT.builder().build());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    public static class EventT extends Event<Object> {

    }
}
