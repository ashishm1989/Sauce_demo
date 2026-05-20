package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * ConfigReader — singleton that loads config.properties once and exposes
 * typed getter methods. System properties override file values so that
 * Maven -D flags (e.g. -Dbrowser=firefox) work transparently.
 */
public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigReader instance;
    private final Properties props = new Properties();

    /* ------------------------------------------------------------------ */
    /*  Singleton                                                           */
    /* ------------------------------------------------------------------ */

    private ConfigReader() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("Cannot find " + CONFIG_FILE + " on the classpath.");
            }
            props.load(is);
            log.info("Config loaded from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /* ------------------------------------------------------------------ */
    /*  Core getter (System property wins over file)                        */
    /* ------------------------------------------------------------------ */

    public String get(String key) {
        String sysProp = System.getProperty(key);
        return (sysProp != null && !sysProp.isBlank()) ? sysProp : props.getProperty(key, "");
    }

    /* ------------------------------------------------------------------ */
    /*  Typed convenience getters                                           */
    /* ------------------------------------------------------------------ */

    public String getBaseUrl()         { return get("base.url"); }
    public String getValidUsername()   { return get("valid.username"); }
    public String getValidPassword()   { return get("valid.password"); }
    public String getBrowser()         { return get("browser"); }
    public boolean isHeadless()        { return Boolean.parseBoolean(get("headless")); }
    public int getImplicitWait()       { return Integer.parseInt(get("implicit.wait")); }
    public int getExplicitWait()       { return Integer.parseInt(get("explicit.wait")); }
    public int getPageLoadTimeout()    { return Integer.parseInt(get("page.load.timeout")); }
    public boolean isScreenshotOnFailure() { return Boolean.parseBoolean(get("screenshot.on.failure")); }
    public String getScreenshotDir()   { return get("screenshot.dir"); }
    public String getReportPath()      { return get("report.path"); }
    public int getRetryCount()         { return Integer.parseInt(get("retry.count")); }
    public String getCheckoutFirstName() { return get("checkout.firstname"); }
    public String getCheckoutLastName()  { return get("checkout.lastname"); }
    public String getCheckoutZip()       { return get("checkout.zipcode"); }

    public List<String> getProductsToAdd() {
        String raw = get("products.to.add");
        return Arrays.asList(raw.split("\\s*,\\s*"));
    }
}
