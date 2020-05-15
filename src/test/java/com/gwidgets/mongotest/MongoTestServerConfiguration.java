package com.gwidgets.mongotest;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

import java.net.InetSocketAddress;

/**
 * mongo-java-server
 * https://github.com/bwaldvogel/mongo-java-server
 * https://github.com/bwaldvogel/mongo-java-server/issues/125
 */
@TestConfiguration
public class MongoTestServerConfiguration
{
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }

    @Bean
    public MongoDbFactory mongoDbFactory(MongoServer mongoServer) {
        InetSocketAddress serverAddress = mongoServer.getLocalAddress();
        return new SimpleMongoClientDbFactory("mongodb://" + serverAddress.getHostName() + ":" + serverAddress.getPort() + "/test");
    }

    @Bean(destroyMethod = "shutdown")
    public MongoServer mongoServer() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return mongoServer;
    }

    @Bean(destroyMethod="close")
    public MongoClient mongoClient(MongoServer mongoServer) {
        return MongoClients.create("mongodb://" + mongoServer.getLocalAddress().getHostName() + ":" + mongoServer.getLocalAddress().getPort());
    }
}
