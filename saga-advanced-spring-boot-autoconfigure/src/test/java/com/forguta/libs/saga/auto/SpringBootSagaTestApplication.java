package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.process.annotation.Processor;
import com.forguta.libs.saga.core.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
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
    public static class SameTest {

        private final EventPublisher eventPublisher;

        @PostConstruct
        public void init() {
            eventPublisher.sendAndForget(Event.builder().body(SampleCreateEvent.builder().build()).build());
        }

        @Processor(SampleCreateEvent.class)
        public void run(SampleCreateEvent sampleCreateEvent){
            System.out.println("sampleCreateEvent = " + sampleCreateEvent);;
        }
    }

}
