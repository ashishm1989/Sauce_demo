package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

 //RetryAnalyzer — automatically retries failed tests up to a configurable
 //maximum number of times.
 
 
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES = ConfigReader.getInstance().getRetryCount();

    
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            log.warn("Retrying test '{}' — attempt {}/{} after failure: {}",
                    result.getName(),
                    retryCount,
                    MAX_RETRIES,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown");
            return true;
        }
        log.error("Test '{}' failed after {} retries.", result.getName(), MAX_RETRIES);
        return false;
    }
}
