package cn.chen.search.controller;

import cn.chen.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author haixin
 * @time 2019-11-04
 */
@RestController
@RequestMapping(value = "search")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 导入数据
     * @return
     */
    @GetMapping("import")
    public Result search(){
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入数据到索引库中成功！");
    }
    /**
     * 搜索商品
     * @return
     */
    @PostMapping
    public Map search(@RequestBody(required = false) Map searchMap){
        return  skuService.search(searchMap);
    }
}