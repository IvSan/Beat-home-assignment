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
        int keysNumber = 300;
        int updatesNumberPerKey = 300;
        ConcurrentOrderedExecutor<Long> underTest = new ConcurrentOrderedExecutor<>();
        Map<Long, List<Integer>> verificationData = new ConcurrentHashMap<>();

        // consecutive updates
        final Long zeroKey = 0L;
        for (int i = 0; i < updatesNumberPerKey; i++) {
            final Integer value = i;
            underTest.planTask(zeroKey, () -> {
                if (!verificationData.containsKey(zeroKey)) verificationData.put(zeroKey, new ArrayList<>());
                var list = verificationData.get(zeroKey);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add(value);
            });
        }

        // parallel updates
        for (int i = 0; i < updatesNumberPerKey; i++) {
            for (long k = 1; k < keysNumber; k++) {
                final Long key = k;
                final Integer value = i;
                underTest.planTask(k, () -> {
                    if (!verificationData.containsKey(key)) verificationData.put(key, new ArrayList<>());
                    var list = verificationData.get(key);
                    try {
                        Thread.sleep(0, 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    list.add(value);
                });
            }
        }

        underTest.close();

        var expected = expectedList(updatesNumberPerKey);
        for (Map.Entry<Long, List<Integer>> entry : verificationData.entrySet()) {
            assertEquals(expected, entry.getValue(), "Mismatch for key=" + entry.getKey());
        }
    }

    private List<Object> expectedList(int updatesNumberWithinOneKey) {
        var result = new ArrayList<>();
        for (int i = 0; i < updatesNumberWithinOneKey; i++) {
            result.add(i);
        }
        return result;
    }
}