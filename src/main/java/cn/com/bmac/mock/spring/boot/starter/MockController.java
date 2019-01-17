package cn.com.bmac.mock.spring.boot.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 * @author CHAN
 * @since 2019-01-15
 */
@RestController
@RequestMapping(path = "${bmac.mock.base-uri:/mock}")
public class MockController {

    private final OuterMockDataManager outerMockDataManager;

    @Autowired
    public MockController(OuterMockDataManager outerMockDataManager) {
        this.outerMockDataManager = outerMockDataManager;
    }

    @PostMapping("/update")
    public Map upd(@RequestBody Map map) {
        outerMockDataManager.setAll(map);
        return outerMockDataManager.getAll();
    }

    @GetMapping("/query")
    public Map query(){
        return outerMockDataManager.getAll();
    }

}
