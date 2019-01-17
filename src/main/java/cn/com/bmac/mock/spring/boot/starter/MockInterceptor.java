package cn.com.bmac.mock.spring.boot.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        logger.info("request uri: {}.", requestURI);

        ServletInputStream inputStream = request.getInputStream();

        Map outerMockData = (Map) outerMockDataManager.getOuterMockData(requestURI);
        if (Objects.isNull(outerMockData)) {
            return true;
        } else {
            MockDataCustomizer mockDataCustomizer = customizers.getOrDefault(requestURI, new DefaultMockDataCustomizer());

            ResolvableType resolvableType = ResolvableType.forClass(mockDataCustomizer.getClass());
            Class<?> resolve = resolvableType.getInterfaces()[0].getGeneric(0).resolve();

            Object requestObj = null;
            boolean hasErrors = false;
            try {
                requestObj = objectMapper.readValue(inputStream, resolve);
            } catch (Exception e) {
                if (e instanceof MismatchedInputException) {
                    logger.warn("the request body may be empty. {}", e.getMessage());
                    if (resolve.isAssignableFrom(Map.class)) {
                        requestObj = new HashMap<>();
                    } else {
                        try {
                            requestObj = resolve.newInstance();
                        } catch (Exception ex) {
                            hasErrors = true;
                        }
                    }
                }else {
                    hasErrors = true;
                }
            }
            if(hasErrors){
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("json parser error. please check your request body.");
                response.flushBuffer();
                return false;
            }

            Map respData = mockDataCustomizer.customize(requestObj);

            respData.putAll(outerMockData);

            String responseDataStr = objectMapper.writeValueAsString(respData);
            logger.info("response data: {}", responseDataStr);

            response.getWriter().write(responseDataStr);
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
