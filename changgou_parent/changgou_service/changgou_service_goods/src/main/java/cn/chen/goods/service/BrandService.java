package cn.chen.goods.service;

import cn.chen.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author haixin
 * @time 2019-10-29
 */
public interface BrandService {

    List<Brand> findAll();

    Brand findById(Integer id);

    void add(Brand brand);

    void update(Brand brand);

    void delete(Integer id);


    /**
     * @param brand
     * @return
     * 多条件查询
     */
    List<Brand> findList(Brand brand);

    PageInfo<Brand> findPage(int page, int size);

    PageInfo<Brand> findPage(Brand brand, int page, int size);
}
