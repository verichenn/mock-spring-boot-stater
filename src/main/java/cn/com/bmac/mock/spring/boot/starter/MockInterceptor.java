package cn.com.bmac.mock.spring.boot.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟数据
 * @author CHAN
 * @since 2019-01-15
 */
public class MockInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, MockDataCustomizer> customizers = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    private final OuterMockDataManager outerMockDataManager;

    private ObjectMapper objectMapper;

    MockInterceptor(ApplicationContext applicationContext, OuterMockDataManager outerMockDataManager, ObjectMapper objectMapper) {
        this.applicationContext = applicationContext;
        this.outerMockDataManager = outerMockDataManager;
        this.objectMapper = objectMapper;
        this.collectMockDataCustomizer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return doHandle(request, response);
    }

    private boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        String requestURI = request.getRequestURI();
        logger.info("request uri was {}.",requestURI);

        ServletInputStream inputStream = request.getInputStream();

        Map outerMockData = (Map) outerMockDataManager.getOuterMockData(requestURI);
        if (Objects.isNull(outerMockData)) {
            return true;
        } else {

            Map requestParams;
            try {
                requestParams = objectMapper.readValue(inputStream, Map.class);
            } catch (Exception e) {
                logger.error("json parser exception.", e);
                requestParams = new HashMap();
            }

            MockDataCustomizer mockDataCustomizer = customizers.getOrDefault(requestURI, new DefaultMockDataCustomizer());

            Map respData = mockDataCustomizer.customize(requestParams);

            respData.putAll(outerMockData);

            response.getWriter().write(objectMapper.writeValueAsString(respData));
            response.flushBuffer();
            return false;
        }

    }


    private void collectMockDataCustomizer() {
        Map<String, MockDataCustomizer> beansOfType = applicationContext.getBeansOfType(MockDataCustomizer.class);
        beansOfType.values().forEach(mock -> {
            Class<? extends MockDataCustomizer> mockClass = mock.getClass();
            boolean annotationPresent = mockClass.isAnnotationPresent(MockTarget.class);
            if (annotationPresent) {
                MockTarget annotation = mockClass.getAnnotation(MockTarget.class);
                String targetURI = annotation.value();
                customizers.put(targetURI, mock);
            }
        });
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

}
