package com.poli.internship.data.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class MessageChannels {
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

}
