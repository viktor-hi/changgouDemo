package cn.chen.order.service;

/**
 * @author haixin
 * @time 2019-11-15
 */
public interface CartService {
    /***
     * 添加购物车
     * @param num:购买商品数量
     * @param skuId：购买商品的skuId
     * @param username：购买用户
     * @return
     */
    void add(Integer num, Long skuId, String username);
}
