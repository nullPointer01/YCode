package com.ym.ycode;

import java.util.Scanner;

import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class YCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(YCodeApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(DeepSeekChatModel deepSeekChatModel, ChatMemory chatMemory,
                                        SessionMemoryAdvisor sessionMemoryAdvisor,
                                        SessionMemoryAdvisor sessionMemorySummarizationAdvisor,
                                        ToolService toolService) {
        return args -> {
            //对话记忆 advisor(会话拦截器，结构化输入)
            ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                    .defaultAdvisors(sessionMemorySummarizationAdvisor, SimpleLoggerAdvisor.builder().build() )
//                            MessageChatMemoryAdvisor.builder(chatMemory).build())
                    .defaultTools(ShellTools.builder().build(), FileSystemTools.builder().build())
                    .build();
            // Start the chat loop
            System.out.println("\n我是YCode:.\n");

            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("\n> 你: ");
                    String userMessage = scanner.nextLine();
                    System.out.println("\n> AI: ");
                    Flux<String> content = chatClient.prompt().user(userMessage).advisors(
                            a -> a.param(ChatMemory.CONVERSATION_ID,"YCode")
                    ).stream().content();
                    content.doOnNext(System.out::print).blockLast();
                }
            }
        };
    }
}
