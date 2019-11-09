package cn.chen.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author haixin
 * @time 2019-11-06
 */
@FeignClient(name = "search")
@RequestMapping("search")
public interface SkuFeign {
    /**
     * 搜索商品
     * @return
     */
    @GetMapping
    Map search(@RequestParam(required = false) Map searchMap);
}
