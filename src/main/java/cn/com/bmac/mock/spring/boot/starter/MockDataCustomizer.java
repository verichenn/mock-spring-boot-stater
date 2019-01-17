package cn.com.bmac.mock.spring.boot.starter;

import java.util.Map;

/**
 * <p>模拟数据类</p>
 * 当你的自定义模拟数据类是实现了{@link MockDataCustomizer}并标注了@{@link MockTarget}时，
 * {@link MockInterceptor}将会根据你的请求uri自动回调你的{@code customize}
 * 方法获取返回响应。
 * @author CHAN
 * @since 2019-01-15
 */
public interface MockDataCustomizer {

    /**
     * 自定义响应
     * @param requestParams 请求参数
     * @return 响应
     */
    Map customize(Map requestParams);
}
