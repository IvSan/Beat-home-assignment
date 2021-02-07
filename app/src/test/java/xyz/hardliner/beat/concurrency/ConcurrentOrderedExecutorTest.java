package xyz.hardliner.beat.concurrency;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentOrderedExecutorTest {

    @Test
    void shouldSaveOrderWithinOneKey() {
        ConcurrentOrderedExecutor<Long> underTest = new ConcurrentOrderedExecutor<>();
        Map<Long, List<Integer>> verificationData = new ConcurrentHashMap<>();

        for (int i = 0; i < 2000; i++) {
            for (long k = 0; k < 2000; k++) {
                final Long key = k;
                final Integer value = i;
                underTest.planTask(k, () -> {
                    if (!verificationData.containsKey(key)) verificationData.put(key, new ArrayList<>());
                    verificationData.get(key).add(value);
                });
            }
        }
        underTest.close();

        for (Map.Entry<Long, List<Integer>> entry : verificationData.entrySet()) {
            assertEquals(expectedList(), entry.getValue(), "Mismatch for key=" + entry.getKey());
        }
    }

    private List<Object> expectedList() {
        var result = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            result.add(i);
        }
        return result;
    }
}