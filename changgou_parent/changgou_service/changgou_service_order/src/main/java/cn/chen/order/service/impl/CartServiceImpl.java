package cn.chen.order.service.impl;

import cn.chen.goods.feign.SkuFeign;
import cn.chen.goods.feign.SpuFeign;
import cn.chen.goods.pojo.Sku;
import cn.chen.goods.pojo.Spu;
import cn.chen.order.pojo.OrderItem;
import cn.chen.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author haixin
 * @time 2019-11-15
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SkuFeign skuFeign;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SpuFeign spuFeign;

    @Override
    public void add(Integer num, Long skuId, String username) {
        //查询SKU
        Result<Sku> resultSku = skuFeign.findById(skuId);
        if (resultSku != null && resultSku.isFlag()) {
            //获取SKU
            Sku sku = resultSku.getData();
            //获取SPU
            Spu spu = spuFeign.findById(sku.getSpuId()).getData();

            //将SKU转换成OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setSkuId(sku.getId());
            orderItem.setName(sku.getName());
            orderItem.setPrice(sku.getPrice());
            orderItem.setNum(num);
            orderItem.setMoney(num * orderItem.getPrice());       //单价*数量
            orderItem.setPayMoney(num * orderItem.getPrice());    //实付金额
            orderItem.setImage(sku.getImage());
            orderItem.setWeight(sku.getWeight() * num);           //重量=单个重量*数量

            //分类ID设置
            orderItem.setCategoryId1(spu.getCategory1Id());
            orderItem.setCategoryId2(spu.getCategory2Id());
            orderItem.setCategoryId3(spu.getCategory3Id());

            /******
             * 购物车数据存入到Redis
             * namespace = Cart_[username]
             * key=skuId
             * value=OrderItem
             */
            redisTemplate.boundHashOps("Cart_" + username).put(skuId, orderItem);
        }
    }
}
