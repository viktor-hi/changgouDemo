package cn.chen.search.service;

import java.util.Map;

/**
 * @author haixin
 * @time 2019-11-04
 */
public interface SkuService {

    /***
     * 导入SKU数据
     */
    void importSku();
    /***
     * 搜索商品
     * @param searchMap 搜索条件
     * @return 结果集
     */
    Map search(Map<String, String> searchMap);
}