package cn.com.bmac.mock.spring.boot.starter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author CHAN
 * @since 2019-01-15
 */
public class MockDataManager {

    /**
     * key:URI,value:mockData
     */
    private Map<String,Object> mockData = new ConcurrentHashMap<>();

    public Object getMockData(String key){
        return this.mockData.get(key);
    }

    public Map<String,Object> getAll(){
        return this.mockData;
    }

    public Object setMockData(String key, Object val){
        if(Objects.isNull(val)){
            return this.mockData.remove(key);
        }
       return this.mockData.put(key, val);
    }

    public void setAll(Map<String,Object> map){
        map.forEach(this::setMockData);
    }
}
