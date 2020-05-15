package com.gwidgets.mongotest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Import(FlapdoodleMongoConfiguration.class)
@ExtendWith(SpringExtension.class)
@DataMongoTest(excludeAutoConfiguration= {EmbeddedMongoAutoConfiguration.class})
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private final static List<String> USER_ID_LIST = Arrays.asList("b2b1f340-cba2-11e8-ad5d-873445c542a2", "bd5dd3a4-cba2-11e8-9594-3356a2e7ef10");

    private static final Random RANDOM = new Random();


    @BeforeEach
    public void dataSetup() {

        Transaction transaction;

        for (int i = 0; i < 10; i++) {
            String requestId = UUID.randomUUID().toString();
            if (i % 2 == 0) {
                transaction = new Transaction(requestId, true, USER_ID_LIST.get(RANDOM.nextInt(2)), System.currentTimeMillis());
            } else {
                transaction = new Transaction(requestId, false, USER_ID_LIST.get(RANDOM.nextInt(2)), System.currentTimeMillis());
            }

            transactionRepository.save(transaction);
        }

    }

    @DisplayName("This is a test")
    @Test
    public void findSuccessfullOperationsForUserWithCreatedDateLessThanNowTest() {
        long now = System.currentTimeMillis();
        String userId = USER_ID_LIST.get(RANDOM.nextInt(2));
        List<Transaction> resultsPage =  transactionRepository.findBySuccessIsTrueAndCreatedLessThanEqualAndUserIdOrderByCreatedDesc(now, userId, PageRequest.of(0, 5)).getContent();

        assertThat(resultsPage).isNotEmpty();
        assertThat(resultsPage).extracting("userId").allMatch(id -> Objects.equals(id, userId));
        assertThat(resultsPage).extracting("created").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(resultsPage).extracting("created").first().matches(createdTimeStamp -> (Long)createdTimeStamp <= now);
        assertThat(resultsPage).extracting("success").allMatch(sucessfull -> (Boolean)sucessfull == true);
    }

}