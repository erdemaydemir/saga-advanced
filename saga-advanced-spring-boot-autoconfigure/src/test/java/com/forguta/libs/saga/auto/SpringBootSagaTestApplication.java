package com.forguta.libs.saga.auto;

import com.forguta.commons.constant.LogSummonerEnum;
import com.forguta.commons.constant.LogSummonerPhaseEnum;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.process.annotation.Processor;
import com.forguta.libs.saga.core.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringBootSagaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSagaTestApplication.class, args);
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    public static class SameTest {

        private final EventPublisher eventPublisher;

        @PostConstruct
        public void init() {
            eventPublisher.sendAndForget(Event.builder().body(SampleCreateEvent.builder().build()).build());
        }

        @Processor(SampleCreateEvent.class)
        public void run(SampleCreateEvent sampleCreateEvent){
            log.info("TYPE={}, PHASE={}, SAMPLE_CREATE_EVENT={}", LogSummonerEnum.LOGIC, LogSummonerPhaseEnum.POST_HANDLE, sampleCreateEvent);
        }
    }

}
