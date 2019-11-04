package cn.chen;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author haixin
 * @time 2019-10-29
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "cn.chen.goods.dao")
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }

    /**
     * 注册IdWorker实例对象
     * @return
     */
    @Bean
    public IdWorker getIdWorker(){
        //IdWorker(工作机器id,数据中心id)
        return new IdWorker(0, 0);
    }
}
