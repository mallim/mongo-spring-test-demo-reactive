package com.gwidgets.mongotest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Integration tests showing MongoDB's reactive driver behavior.
 * Test case form https://github.com/mp911de/under-the-hood-reactive
 */
@SpringBootTest(classes = MongoTestServerConfiguration.class)
// @SpringBootTest(classes = FlapdoodleMongoConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AllArgsConstructor
@Slf4j
public class MongoDBIntegrationTests
{
    private static final int ITEM_COUNT = 109;

    private final ReactiveMongoOperations mongoOperations;

    @BeforeEach
    public void before() {

        Flux<Person> people = mongoOperations.dropCollection(Person.class)
                .thenMany(Flux.fromStream(People.stream()) //
                        .buffer(20) //
                        .flatMap(mongoOperations::insertAll ) );

        StepVerifier.create(people) //
                .expectNextCount(ITEM_COUNT) //
                .verifyComplete();
        System.out.println();
        System.out.println();
    }

    /**
     * Read from {@link com.mongodb.reactivestreams.client.MongoCollection} with request(unbounded).
     */
    @Test
    public void findRequestUnboundedFromMongoCollection() {

        log.info("findRequestUnboundedFromCollection: Find all using MongoCollection (Driver)");

        Publisher<Document> find = mongoOperations.getCollection("person").find();

        StepVerifier.create(find) //
                .expectNextCount(ITEM_COUNT) //
                .verifyComplete();
    }

    /**
     * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(unbounded).
     */
    @Test
    public void findRequestUnbounded() {

        log.info("findRequestUnbounded: Find all via ReactiveMongoOperations (Spring Data)");

        Flux<Person> find = mongoOperations.findAll(Person.class);

        StepVerifier.create(find) //
                .expectNextCount(ITEM_COUNT) //
                .verifyComplete();
    }

    /**
     * Read from {@link ReactiveMongoOperations#findAll(Class)} v
     */
    @Test
    public void findRequestTen() {

        log.info("findRequestTen: Find all via ReactiveMongoOperations (Spring Data)");

        Flux<Person> find = mongoOperations.findAll(Person.class);

        StepVerifier.create(find, 10).expectNextCount(10) //
                .thenAwait()//
                .thenRequest(10).expectNextCount(10) //
                .thenCancel().verify();
    }

    /**
     * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(10) and a {@link Flux#filter(Predicate)}
     * operator.
     */
    @Test
    public void findRequestWithFilter() {

        log.info("findRequestWithFilter: Find all via findAll and filter(…) operator");

        Flux<Person> find = mongoOperations.findAll(Person.class) //
                .filter(p -> p.name.length() > 20);

        StepVerifier.create(find, 10) //
                .expectNextCount(7) //
                .verifyComplete();
    }

    /**
     * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(10) and a {@link Flux#filter(Predicate)}
     * operator. Additionally adding {@link Flux#limitRate(int)} before {@link Flux#filter(Predicate)} for asynchronous
     * prefetching.
     */
    @Test
    public void findRequestWithFilterAndPrefetch() {

        log.info("findRequestWithFilterAndPrefetch: Find all via findAll and filter(…) using limitRate(20)");

        Flux<Person> find = mongoOperations.findAll(Person.class) //
                // .log("example.filter", Level.INFO, SignalType.REQUEST) //
                .limitRate(20) //
                .filter(p -> p.name.length() > 20);

        StepVerifier.create(find, 10) //
                .expectNextCount(7) //
                .verifyComplete();
    }

    /**
     * Read from {@link ReactiveMongoOperations#find(Query, Class)} with request(10) and a {@link Flux#filter(Predicate)}
     * operator. Additionally adding {@link Query#cursorBatchSize(int)} to control the batch size.
     */
    @Test
    public void findRequestWithFilterAndBatchSize() {

        log.info("findRequestWithFilterAndBatchSize: Find all via find and filter(…) using cursorBatchSize(20)");

        Flux<Person> find = mongoOperations.find(new Query().cursorBatchSize(20), Person.class) //
                // .log("example.filter", Level.INFO, SignalType.REQUEST)
                // limit rate reduces initial prefetch by prefetch >> 2 for smart pre-buffering
                // .limitRate(5) - uncomment to issue multiple requests of which the most are served from the recv buffer
                .filter(p -> p.name.length() > 20);

        StepVerifier.create(find, 10) //
                .expectNextCount(7) //
                .verifyComplete();
    }
}
