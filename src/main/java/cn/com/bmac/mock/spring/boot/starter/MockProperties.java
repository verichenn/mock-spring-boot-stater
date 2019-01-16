package cn.com.bmac.mock.spring.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author CHAN
 * @since 2019-01-15
 */
@ConfigurationProperties(prefix = "bmac.mock")
public class MockProperties {

    private String pathPatterns = "/**";

    public String getPathPatterns() {
        return pathPatterns;
    }

    public void setPathPatterns(String pathPatterns) {
        this.pathPatterns = pathPatterns;
    }
}
