package cn.chen.goods.feign;

import cn.chen.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author haixin
 * @time 2019-11-15
 */
@FeignClient(name = "goods")
@RequestMapping("spu")
public interface SpuFeign {
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable Long id);
}