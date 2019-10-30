package cn.chen.goods.controller;

import cn.chen.goods.pojo.Brand;
import cn.chen.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author haixin
 * @time 2019-10-29
 */
@RestController
@RequestMapping("brand")
//用于解决js跨域问题-在微服务中，一般都需要此注解
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> brands = brandService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",brands);
    }

    @GetMapping("{id}")
    public Result<Brand> findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",brand);
    }

    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    @PutMapping
    public Result updata(@RequestBody Brand brand){
        brandService.update(brand);
        return new Result(true,StatusCode.OK,"修改品牌成功");
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除品牌成功");
    }

    @PostMapping("search")
    public Result<List<Brand>> search(@RequestBody(required = false) Brand brand){
        List<Brand> list = brandService.findList(brand);
        return new Result<List<Brand>>(true, StatusCode.OK, "查询品牌成功",list);
    }

    @GetMapping("search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@PathVariable Integer page, @PathVariable Integer size){
        PageInfo<Brand> info = brandService.findPage(page, size);
        return new Result<PageInfo<Brand>>(true, StatusCode.OK, "查询品牌成功",info);
    }

    @PostMapping("search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@RequestBody Brand brand,@PathVariable Integer page, @PathVariable Integer size){
        PageInfo<Brand> info = brandService.findPage(brand,page, size);
//        int i = 1 / 0;
        return new Result<PageInfo<Brand>>(true, StatusCode.OK, "查询品牌成功",info);
    }
}
