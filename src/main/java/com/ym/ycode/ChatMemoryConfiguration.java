package com.ym.ycode;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.session.compaction.RecursiveSummarizationCompactionStrategy;
import org.springframework.ai.session.compaction.SlidingWindowCompactionStrategy;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yuanming.zhang
 * @Since 2026/10/26
 **/
@Configuration
public class ChatMemoryConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                        .maxMessages(20)
                        .chatMemoryRepository(chatMemoryRepository).build();
    }

    @Bean
    SessionMemoryAdvisor sessionMemoryAdvisor() {
        SessionService service = DefaultSessionService.builder()
                .sessionRepository(InMemorySessionRepository.builder().build())
                .build();

        return SessionMemoryAdvisor.builder(service)
                .defaultUserId("YCode")
                .compactionTrigger(new TurnCountTrigger(1))
                .compactionStrategy(SlidingWindowCompactionStrategy.builder()
                        .maxEvents(1)
                        .build())
                .build();
    }

    @Bean
    SessionMemoryAdvisor sessionMemorySummarizationAdvisor(DeepSeekChatModel deepSeekChatModel) {
        SessionService service = DefaultSessionService.builder()
                .sessionRepository(InMemorySessionRepository.builder().build())
                .build();

        return SessionMemoryAdvisor.builder(service)
                .defaultUserId("YCode")
                .compactionTrigger(new TurnCountTrigger(10))
                .compactionStrategy(RecursiveSummarizationCompactionStrategy.builder(ChatClient.builder(deepSeekChatModel).build())
                        .maxEventsToKeep(2).overlapSize(0)
                        .build())
                .build();
    }
}
