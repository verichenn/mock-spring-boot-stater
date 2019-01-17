package cn.com.bmac.mock.spring.boot.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * webmvc配置类
 * @author CHAN
 * @since 2019-01-15
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(MockProperties.class)
@ConditionalOnProperty(value = "bmac.mock.enable", havingValue = "true",matchIfMissing = true)
public class MockAutoConfiguration {

    @Autowired
    private MockProperties mockProperties;

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public WebMvcConfigurer interceptorConfigurer(ApplicationContext applicationContext) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                HandlerInterceptor mockInterceptor = new MockInterceptor(applicationContext, outerMockDataManager(), objectMapper);
                registry.addInterceptor(mockInterceptor)
                        .addPathPatterns(mockProperties.getPathPatterns());
            }
        };
    }

    @Bean
    public OuterMockDataManager outerMockDataManager(){
        return new OuterMockDataManager();
    }

    @Bean
    public MockController mockController(){
        return new MockController(outerMockDataManager());
    }
}
