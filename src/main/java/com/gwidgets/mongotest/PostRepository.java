package com.gwidgets.mongotest;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveMongoRepository<Post, String>
{
    Flux<PostSummary> findByTitleContains(String title);
}
