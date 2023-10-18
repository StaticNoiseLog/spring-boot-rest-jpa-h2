package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
public class EventListenerExampleBean {
    private static final Logger LOG = Logger.getLogger(EventListenerExampleBean.class.getName());

    @Autowired
    private CatRepository catRepository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Starting with 3 cats!");
        Stream.of("Felix", "Garfield", "Whiskers")
                .forEach(n -> catRepository.save(new Cat(n)));
    }
}