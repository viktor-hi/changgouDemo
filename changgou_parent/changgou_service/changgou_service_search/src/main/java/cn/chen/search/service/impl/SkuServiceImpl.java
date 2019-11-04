package cn.chen.search.service.impl;

import cn.chen.search.dao.SkuEsMapper;
import cn.chen.search.service.SkuService;
import cn.chen.goods.feign.SkuFeign;
import cn.chen.goods.pojo.Sku;
import cn.chen.search.pojo.SkuInfo;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haixin
 * @time 2019-11-04
 */
@Service
public class SkuServiceImpl implements SkuService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate esTemplate;
    /**
     * 导入sku数据到es
     */
    @Override
    public void importSku(){
        //调用changgou-service-goods微服务
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        //将数据转成search.Sku
        List<SkuInfo> skuInfos=  JSON.parseArray(JSON.toJSONString(skuListResult.getData()),SkuInfo.class);
        for(SkuInfo skuInfo:skuInfos){
            Map<String, Object> specMap= JSON.parseObject(skuInfo.getSpec()) ;
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    @Override
    public Map search(Map<String, String> searchMap) {
        HashMap<Object, Object> map = new HashMap<>();
        //1、构建基本查询条件
        NativeSearchQueryBuilder builder = builderBasicQuery(searchMap);
        //2、根据查询条件-搜索商品列表
        searchList(map, builder);
        return map;
    }


    /**
     * 根据查询条件-搜索商品列表
     * @param map 结果集
     * @param builder 查询条件构建器
     */
    private void searchList(Map map, NativeSearchQueryBuilder builder) {
        //3、获取NativeSearchQuery搜索条件对象-builder.build()
        NativeSearchQuery query = builder.build();
        //4.查询数据-esTemplate.queryForPage(条件对象,搜索结果对象)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class);
        //5、包装结果并返回
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
    }

    /**
     * 构建基本查询条件
     * @param searchMap 用户传入的查询参数
     * @return 查询条件构建器
     */
    private NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        //1、创建查询条件构建器-builder = new NativeSearchQueryBuilder()
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //2、组装查询条件
        if(searchMap != null){
            //2.1关键字搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
            //如果用户传入了关键字
            if(StringUtils.isNotEmpty(keywords)){
                //查询name域
                builder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
        }
        return builder;
    }
}
