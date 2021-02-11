package xyz.hardliner.beat.e2e;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static java.nio.file.Files.deleteIfExists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static xyz.hardliner.beat.e2e.InputDatasetGenerator.prepareMockDataset;

public class E2eTestRunner {

    @Test
    void shouldProcessGivenData() throws ParseException, IOException {
        var app = new App();
        app.main(new String[]{
            "-i", "./src/test/resources/given_paths.csv",
            "-o", "./src/test/resources/output_for_given_paths.csv"
        });

        var expected = "./src/test/resources/expected_output_for_given_paths.csv";
        var actual = "./src/test/resources/output_for_given_paths.csv";

        assertEquals(readFileToSetOfLines(expected), readFileToSetOfLines(actual));

        deleteIfExists(Path.of(actual));
    }

    private Set<String> readFileToSetOfLines(String path) throws FileNotFoundException {
        var result = new HashSet<String>();
        var sc = new Scanner(new File(path));
        while (sc.hasNext()) {
            result.add(sc.nextLine());
        }
        return result;
    }

    @Test
    void shouldProcessRandomData() throws ParseException, IOException {

        prepareMockDataset("./src/test/resources/generated_paths.csv", 1000);

        var app = new App();
        app.main(new String[]{
            "-i", "./src/test/resources/generated_paths.csv",
            "-o", "./src/test/resources/output_for_generated_paths.csv"
        });

        var actual = new File("./src/test/resources/output_for_generated_paths.csv");

        assertFalse(FileUtils.readFileToString(actual, "utf-8").isBlank());

        deleteIfExists(Path.of("./src/test/resources/generated_paths.csv"));
        deleteIfExists(actual.toPath());
    }

}
