package cn.com.bmac.mock.spring.boot.starter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author CHAN
 * @since 2019-01-15
 */
public class OuterMockDataManager {

    /**
     * key:URI,value:outerMockData
     */
    private Map<String,Object> outerMockData = new ConcurrentHashMap<>();

    public Object getOuterMockData(String key){
        return this.outerMockData.get(key);
    }

    public Map<String,Object> getAll(){
        return this.outerMockData;
    }

    public Object setOuterMockData(String key, Object val){
        if(Objects.isNull(val)){
            return this.outerMockData.remove(key);
        }
       return this.outerMockData.put(key, val);
    }

    public void setAll(Map<String,Object> map){
        map.forEach(this::setOuterMockData);
    }
}
