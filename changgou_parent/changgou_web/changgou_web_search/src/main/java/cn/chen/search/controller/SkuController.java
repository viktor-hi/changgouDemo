package cn.chen.search.controller;

/**
 * @author haixin
 * @time 2019-11-07
 */

import cn.chen.search.feign.SkuFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Controller
@RequestMapping(value = "search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign;
    /**
     * 搜索商品
     * 注意此处的@GetMapping()要添加list的url请求，不然会跟SkuFeign中的请求url冲突
     */
    @GetMapping("list")
    public String search(@RequestParam(required = false) Map searchMap, Model model){
        //查询数据
        Map result = skuFeign.search(searchMap);
        //返回查询结果
        model.addAttribute("result",result);
        //返回查询条件
        model.addAttribute("searchMap",searchMap);
        //响应视图
        return "search";
    }
}
