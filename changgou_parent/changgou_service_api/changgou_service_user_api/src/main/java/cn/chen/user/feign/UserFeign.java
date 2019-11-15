package cn.chen.user.feign;

import cn.chen.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author haixin
 * @time 2019-11-14
 */
@FeignClient(name = "user")
@RequestMapping("user")
public interface UserFeign {
    /***
     * 根据ID查询User数据
     */
    @GetMapping("load/{id}")
    Result<User> findById(@PathVariable String id);
}
