package com.gwidgets.mongotest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.TestConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case from https://github.com/shazin/spring-data-reactive-mongodb-tutorial
 */
@SpringBootTest(classes = FlapdoodleMongoConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AllArgsConstructor
@Slf4j
public class ReactiveTaxiRepositoryTest
{
    private final ReactiveTaxiRepository reactiveTaxiRepository;

    private final ReactiveMongoOperations reactiveMongoOperations;

    @BeforeEach
    public void setup() {
        reactiveMongoOperations.collectionExists(Taxi.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(Taxi.class) : Mono.just(exists))
                .flatMap(o -> reactiveMongoOperations.createCollection(Taxi.class, CollectionOptions.empty().size(1024 * 1024).maxDocuments(100).capped()))
                .then()
                .block();

        reactiveTaxiRepository.saveAll(
                Flux.just(
                        new Taxi(UUID.randomUUID().toString(), "CAL-4259", 4),
                        new Taxi(UUID.randomUUID().toString(), "BCN-8542", 4)))
                .then()
                .block();
    }

    @Test
    public void testFindByNumber() {
        List<Taxi> myTaxis = reactiveTaxiRepository.findByNumber("CAL-4259")
                .collectList()
                .block();

        assertThat(myTaxis)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testFindWithTailableCursorBy() throws Exception {
        Disposable subscription = reactiveTaxiRepository.findWithTailableCursorBy()
                .doOnNext(System.out::println)
                .doOnComplete(() -> System.out.println("Finished"))
                .doOnTerminate(() -> System.out.println("Terminated"))
                .subscribe();

        Thread.sleep(1000);

        reactiveTaxiRepository.save(new Taxi(UUID.randomUUID().toString(), "ABC-1234", 4)).subscribe();
        Thread.sleep(100);

        reactiveTaxiRepository.save(new Taxi(UUID.randomUUID().toString(), "XYZ-1234", 4)).subscribe();
        Thread.sleep(1000);

        subscription.dispose();

        reactiveTaxiRepository.save(new Taxi(UUID.randomUUID().toString(), "DEF-1234", 4)).subscribe();
        Thread.sleep(100);

    }
}
