package com.gwidgets.mongotest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.TestConstructor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case from https://github.com/hantsy/spring-reactive-sample
 */
@SpringBootTest(classes = MongoTestServerConfiguration.class)
// @SpringBootTest(classes = FlapdoodleMongoConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AllArgsConstructor
@Slf4j
public class PostRepositoryTest
{
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final PostRepository postRepository;

    @BeforeEach
    public void setup() {
        this.reactiveMongoTemplate.remove(Post.class).all()
                .subscribe(r -> log.debug("delete all posts: " + r), e -> log.debug("error: " + e), () -> log.debug("done"));
    }

    @Test
    public void testSavePostAndFindByTitleContains() {
        this.postRepository.save(Post.builder().content("my test content").title("my test title").build())
                .flatMapMany(p->this.postRepository.findByTitleContains("test"))
                .as(StepVerifier::create)
                .consumeNextWith(p -> assertThat(p.getTitle()).isEqualTo("my test title"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testSavePost() {
        StepVerifier.create(this.postRepository.save(Post.builder().content("my test content").title("my test title").build()))
                .consumeNextWith(p -> assertThat(p.getTitle()).isEqualTo("my test title"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testSaveAndVerifyPost() {
        Post saved = this.postRepository.save(Post.builder().content("my test content").title("my test title").build()).block();
        assertThat(saved.getId()).isNotNull();
        assertThat(this.reactiveMongoTemplate.collectionExists(Post.class).block()).isTrue();
        assertThat(this.reactiveMongoTemplate.findById(saved.getId(), Post.class).block().getTitle()).isEqualTo("my test title");
    }

    @Test
    public void testGetAllPost() {
        Post post1 = Post.builder().content("my test content").title("my test title").build();
        Post post2 = Post.builder().content("content of another post").title("another post title").build();

        Flux<Post> allPosts = Flux.just(post1, post2)
                .flatMap(this.postRepository::save)
                .thenMany(this.postRepository.findAll(Sort.by((Sort.Direction.ASC), "title")));

        StepVerifier.create(allPosts)
                .expectNextMatches(p -> p.getTitle().equals("another post title"))
                .expectNextMatches(p -> p.getTitle().equals("my test title"))
                .verifyComplete();
    }
}
