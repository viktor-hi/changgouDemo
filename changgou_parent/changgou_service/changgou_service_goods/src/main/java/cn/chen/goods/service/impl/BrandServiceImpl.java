package cn.chen.goods.service.impl;

import cn.chen.goods.dao.BrandMapper;
import cn.chen.goods.pojo.Brand;
import cn.chen.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author haixin
 * @time 2019-10-29
 */
@Service
public class BrandServiceImpl implements BrandService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BrandMapper brandMapper;
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
            return brandMapper.select(brand);
    }

    @Override
    public PageInfo<Brand> findPage(int page, int size) {
        //设置分页条件
        PageHelper.startPage(page, size);
        //分页查询数据列表
        List<Brand> brandList = brandMapper.selectAll();
        //总记录数
        PageInfo<Brand> info = new PageInfo<>(brandList);
        return info;
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, int page, int size) {
        //设置分页条件
        PageHelper.startPage(page, size);
        //分页查询数据列表-方式一
        //List<Brand> brandList = brandMapper.select(brand);

        //方式二：
        //构建查询条件
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(brand != null){
            //" " isNotBlank == false  isNotEmpty == true
            if (StringUtils.isNotBlank(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }
        }

        List<Brand> brandList = brandMapper.selectByExample(example);

        //总记录数
        PageInfo<Brand> info = new PageInfo<>(brandList);
        return info;
    }
}
