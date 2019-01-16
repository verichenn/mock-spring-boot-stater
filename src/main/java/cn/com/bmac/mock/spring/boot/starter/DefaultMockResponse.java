package cn.com.bmac.mock.spring.boot.starter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author CHAN
 * @since 2019-01-16
 */
public class DefaultMockResponse implements MockResponse {
    @Override
    public Map customizeResponse(Map requestParams) {
        return new HashMap();
    }
}
