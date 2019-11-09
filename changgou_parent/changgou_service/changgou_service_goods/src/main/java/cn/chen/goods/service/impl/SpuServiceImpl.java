package cn.chen.goods.service.impl;

import cn.chen.goods.dao.BrandMapper;
import cn.chen.goods.dao.CategoryMapper;
import cn.chen.goods.dao.SkuMapper;
import cn.chen.goods.dao.SpuMapper;
import cn.chen.goods.pojo.*;
import cn.chen.goods.service.SpuService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:sz.itheima
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CategoryMapper categoryMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BrandMapper brandMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SkuMapper skuMapper;

    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id){
        Spu spu = spuMapper.selectByPrimaryKey(id);

        //判断是否被逻辑删除,必须先逻辑删除后才能物理删除
        if (!"1".equalsIgnoreCase(spu.getIsDelete())) {
            throw new RuntimeException("商品删除失败");
        }else {
            spuMapper.deleteByPrimaryKey(id);
        }

    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    @Override
    public void saveGoods(Goods goods) {
        //查看是否有spuId判断是更新还是插入新数据
        Spu spu = goods.getSpu();
        if (spu.getId() == null) {
            //增加Spu

            spu.setId(idWorker.nextId());
            spuMapper.insertSelective(spu);
        }else {
            //更新spu，并删除原本的sku插入新的sku
//            spuMapper.deleteByPrimaryKey(spu.getId());
//            spuMapper.insert(spu);
            spuMapper.updateByPrimaryKeySelective(spu);

            Sku where = new Sku();
            where.setSpuId(spu.getId());
            skuMapper.delete(where);
        }


        //增加Sku
        Date now = new Date();
        //查询分类
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //查询品牌
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //获取Sku集合
        List<Sku> skus = goods.getSkuList();
        //循环将数据加入到数据库
        for (Sku sku : skus) {
            //构建SKU名称，采用SPU+规格值组装
            if(StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }
            //获取Spu的名字
            String name = spu.getName();
            //将规格转换成Map
            Map<String,String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            //循环组装Sku的名字
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                name+="  "+entry.getValue();
            }
            sku.setName(name);

            //分布式ID
            sku.setId(idWorker.nextId());
            //SpuId
            sku.setSpuId(spu.getId());
            //创建日期
            sku.setCreateTime(now);
            //修改日期
            sku.setUpdateTime(now);
            //商品分类ID
            sku.setCategoryId(spu.getCategory3Id());
            //分类名字
            sku.setCategoryName(category.getName());
            //品牌名字
            sku.setBrandName(brand.getName());
            //增加
            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsById(Long spuId) {
        Goods goods = new Goods();
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //查sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);

        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    @Override
    public void audit(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("此商品已被删除，无法审核！");
        }else {
            spu.setStatus("1");
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    @Override
    public void pull(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("此商品已被删除，无法下架！");
        }else {
            spu.setIsMarketable("0");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    @Override
    public void put(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu.getIsDelete().equalsIgnoreCase("1")) {
            throw new RuntimeException("此商品已被删除，无法上架！");
        }if(spu.getStatus().equalsIgnoreCase("0")){
            throw new RuntimeException("此商品未被审核，无法上架！");
        }else {
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    @Override
    public int putMany(Long[] ids) {
        //修改的结果
        Spu spu = new Spu();
        spu.setIsMarketable("1");

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids)); //id
        criteria.andEqualTo("isMarketable", "0"); //下架商品才能上架
        criteria.andEqualTo("status", "1"); //审核通过的
        criteria.andEqualTo("isDelete", "0"); //非删除的
        //第一个参数为修改结果，第二个参数为修改的范围
        int i = spuMapper.updateByExampleSelective(spu, example);
        return i;
    }

    @Override
    public int pullMany(Long[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids)); //id
        criteria.andEqualTo("isMarketable", "1"); //上架商品才能下架
        criteria.andEqualTo("isDelete", "0"); //非删除的

        int i = spuMapper.updateByExampleSelective(spu, example);
        return i;
    }

    /**
     * 实现逻辑删除
     * @param spuId
     */
    @Override
    public void logicDelete(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (!"0".equalsIgnoreCase(spu.getIsMarketable())) {
            throw new RuntimeException("商品需要先下架才可以删除");
        }else {
            spu.setIsDelete("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }


    /**
     * 回复被逻辑删除的商品
     * @param spuId
     */
    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        if (!"1".equalsIgnoreCase(spu.getIsDelete())) {
            throw new RuntimeException("商品没有被删除");
        }else {
            //回复商品
            spu.setIsDelete("0");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }


}
