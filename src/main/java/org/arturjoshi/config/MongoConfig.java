package org.arturjoshi.config;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    public MongoClient mongoClient() {
        return new MongoClient("localhost", 27017);
    }

    @Bean
    public DB db() {
        return mongoClient().getDB("wait_db");
    }
}
