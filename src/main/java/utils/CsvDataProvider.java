package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvDataProvider — reads CSV files from the classpath and returns rows as
 * a 2D Object array suitable for TestNG {@code @DataProvider}.
 *
 * The first row is treated as a header and is skipped.
 */
public class CsvDataProvider {

    private static final Logger log = LoggerFactory.getLogger(CsvDataProvider.class);

    private CsvDataProvider() { /* utility class */ }

    /**
     * Load a CSV from the classpath.
     *
     * @param classpathResource  e.g. {@code "testdata/login_data.csv"}
     * @return 2D array of Strings (header row excluded)
     */
    public static Object[][] loadCsv(String classpathResource) {
        try (InputStream is = CsvDataProvider.class
                .getClassLoader()
                .getResourceAsStream(classpathResource)) {

            if (is == null) {
                throw new RuntimeException("CSV resource not found: " + classpathResource);
            }

            try (CSVReader reader = new CSVReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {

                List<String[]> allRows = reader.readAll();
                if (allRows.isEmpty()) {
                    log.warn("CSV file is empty: {}", classpathResource);
                    return new Object[0][0];
                }

                // Skip header row (index 0)
                List<Object[]> dataRows = new ArrayList<>();
                for (int i = 1; i < allRows.size(); i++) {
                    String[] row = allRows.get(i);
                    // Skip completely blank rows
                    if (row.length == 0 || (row.length == 1 && row[0].isBlank())) {
                        continue;
                    }
                    dataRows.add(row);
                }

                log.info("Loaded {} data rows from {}", dataRows.size(), classpathResource);
                return dataRows.toArray(new Object[0][]);
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("Failed to read CSV: " + classpathResource, e);
        }
    }
}
