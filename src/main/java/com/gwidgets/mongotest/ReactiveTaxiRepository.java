package com.gwidgets.mongotest;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ReactiveTaxiRepository extends ReactiveMongoRepository<Taxi, String>
{
    Flux<Taxi> findByNumber(String number);

    @Tailable
    Flux<Taxi> findWithTailableCursorBy();
}
