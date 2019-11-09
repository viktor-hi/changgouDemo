package cn.chen.search.service.impl;

import cn.chen.search.dao.SkuEsMapper;
import cn.chen.search.service.SkuService;
import cn.chen.goods.feign.SkuFeign;
import cn.chen.goods.pojo.Sku;
import cn.chen.search.pojo.SkuInfo;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;


import java.util.*;

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
        searchList(builder,map);
//        //3、分组查询商品分类列表
//        searchCategoryList(builder,map);
//        //查询商品品牌列表
//        searchBrandList(builder,map);
//        //查询商品规格
//        searchSpec(builder,map);
        //一次分组查询分类、品牌与规格
        searchGroup(builder,map);
        return map;
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
//            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
//            //如果用户传入了关键字
//            if(StringUtils.isNotEmpty(keywords)){
//                //查询name域
//                builder.withQuery(QueryBuilders.matchQuery("name", keywords));
//            }
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //2.1关键字搜索-builder.withQuery(QueryBuilders.matchQuery(域名，内容))
            String keywords = searchMap.get("keywords") == null ? "" : searchMap.get("keywords");
            //如果用户传入了关键字
            if(StringUtils.isNotEmpty(keywords)){
                //查询name域
                //builder.withQuery(QueryBuilders.matchQuery("name", keywords));
                boolQueryBuilder.must(QueryBuilders.matchQuery("name", keywords));
            }

            //分类搜索
            String category = searchMap.get("category") == null ? "" : searchMap.get("category");
            if(StringUtils.isNotEmpty(category)){
                //查询category域
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", category));
            }
            //2.3 品牌查询
            String brand = searchMap.get("brand") == null ? "" : searchMap.get("brand");
            //如果用户传入了品牌
            if(StringUtils.isNotEmpty(brand)){
                //查询brand域
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", brand));
            }

            //规格查询
            //2.4 规格查询
            //读取用户传入的所有参数的key
            for (String key : searchMap.keySet()) {
                //识别规格:用户传入，spec_网络制式：电信4G
                if(key.startsWith("spec_")){
                    //specMap.规格名字.keyword
                    String specField = "specMap." + key.substring(5) + ".keyword";
                    //规格域
                    boolQueryBuilder.must(QueryBuilders.termQuery(specField, searchMap.get(key)));
                }
            }
            //2.5 价格区间查询
//            String price = searchMap.get("price") == null ? "" : searchMap.get("price");
//            //如果用户传入了价格
//            if(StringUtils.isNotEmpty(price)){
//                //范围匹配搜索
//                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
//                //解析前端传入的价格：0-500 ,500-1000,3000
//                String[] split = price.split("-");
//                //处理前面的价格:price >= 0
//                boolQueryBuilder.must(rangeQueryBuilder.gte(split[0]));
//                //如果解析结果大小1，说明传入的不是3000
//                if(split.length > 1){
//                    //price <= 500
//                    boolQueryBuilder.must(rangeQueryBuilder.lte(split[1]));
//                }
//            }
            String price = searchMap.get("price") == null ? "" : searchMap.get("price");
            if (StringUtils.isNotBlank(price)){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");

                String[] split = price.split("-");
                boolQueryBuilder.must(rangeQueryBuilder.gte(split[0]));

                if (split.length > 1) {
                    boolQueryBuilder.must(rangeQueryBuilder.lte(split[1]));
                }
            }
            //一次性添加多个查询条件

            builder.withQuery(boolQueryBuilder);
            //当前页
            Integer page = searchMap.get("pageNum") == null ? 1 : new Integer(searchMap.get("pageNum"));
            Integer pageSize = 10;  //每页查询记录数
            //PageRequest.of(当前页【0开始】，每页查询的条数)
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            //设置分页查询
            builder.withPageable(pageRequest);
//排序
            //排序方式:ASC|DESC
            String sortRule = searchMap.get("sortRule") == null ? "" : searchMap.get("sortRule");
            //排序域名
            String sortField = searchMap.get("sortField") == null ? "" : searchMap.get("sortField");
            if(StringUtils.isNotEmpty(sortField)) {
                //fieldSort(域名)，order(排序方式)
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }

        }
        return builder;
    }


    /**
     * 根据查询条件-搜索商品列表
     * @param map 结果集
     * @param builder 查询条件构建器
     */
    private void searchList(NativeSearchQueryBuilder builder, Map map) {
        //h1.配置高亮查询信息-hField = new HighlightBuilder.Field()
        //h1.1:设置高亮域名-在构造函数中设置
        HighlightBuilder.Field hField = new HighlightBuilder.Field("name");
        //h1.2：设置高亮前缀-hField.preTags
        hField.preTags("<em style='color:red;'>");
        //h1.3：设置高亮后缀-hField.postTags
        hField.postTags("</em>");
        //h1.4：设置碎片大小-hField.fragmentSize
        hField.fragmentSize(100);
        //h1.5：追加高亮查询信息-builder.withHighlightFields()
        builder.withHighlightFields(hField);


        //3、获取NativeSearchQuery搜索条件对象-builder.build()
        NativeSearchQuery query = builder.build();
        //4.查询数据-esTemplate.queryForPage(条件对象,搜索结果对象)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(query, SkuInfo.class,new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //h2.2 先定义一组查询结果列表-List<T> list = new ArrayList<T>()
                List<T> list = new ArrayList<T>();
                //h2.3 遍历查询到的所有高亮数据-response.getHits().for
                for (SearchHit hit : response.getHits()) {
                    //h2.3.1 先获取当次结果的原始数据(无高亮)-hit.getSourceAsString()
                    String json = hit.getSourceAsString();
                    //h2.3.2 把json串转换为SkuInfo对象-skuInfo = JSON.parseObject()
                    SkuInfo skuInfo = JSON.parseObject(json, SkuInfo.class);
                    //h2.3.3 获取name域的高亮数据-nameHighlight = hit.getHighlightFields().get("name")
                    HighlightField nameHighlight = hit.getHighlightFields().get("name");

                    //h2.3.4 如果高亮数据不为空-读取高亮数据
                    if(nameHighlight != null){
                        //h2.3.4.1 定义一个StringBuffer用于存储高亮碎片-buffer = new StringBuffer()
                        StringBuffer buffer = new StringBuffer();
                        //h2.3.4.2 循环组装高亮碎片数据-nameHighlight.getFragments().for(追加数据)
                        for (Text fragment : nameHighlight.getFragments()) {
                            buffer.append(fragment);
                        }
                        //h2.3.4.3 将非高亮数据替换成高亮数据-skuInfo.setName()
                        skuInfo.setName(buffer.toString());
                    }
                    //h2.3.5 将替换了高亮数据的对象封装到List中-list.add((T) skuInfo)
                    list.add((T) skuInfo);
                }
                //h2.4 返回当前方法所需要参数-new AggregatedPageImpl<T>(数据列表，分页选项,总记录数)
                //h2.4 参考new AggregatedPageImpl<T>(list,pageable,response.getHits().getTotalHits())
                return new AggregatedPageImpl<T>(list, pageable, response.getHits().getTotalHits());
            }
        });

        //h2.1实现mapResults(查询到的结果,数据列表的类型,分页选项)方法

        //5、包装结果并返回
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());
        map.put("totalPages", page.getTotalPages());
    }

    private void searchGroup(NativeSearchQueryBuilder builder, Map map) {
        builder.addAggregation(AggregationBuilders.terms("group_category").field("categoryName"));
        builder.addAggregation(AggregationBuilders.terms("group_brand").field("brandName"));
        builder.addAggregation(AggregationBuilders.terms("group_spec").field("spec.keyword").size(1000));
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();

        //1、提取分类结果
        List<String> group_category = getGroupResult(aggregations, "group_category");
        map.put("categoryList", group_category);
        //2、提取品牌名字
        List<String> group_brand = getGroupResult(aggregations, "group_brand");
        map.put("brandList",group_brand);

        //3、提取各种规格
        List<String> group_spec = getGroupResult(aggregations, "group_spec");

        HashMap<String, Set<String>> stringSetHashMap = new HashMap<>();

        for (String spec : group_spec) {
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                String value = specMap.get(key);
                Set<String> hashValue = stringSetHashMap.get(key);
                if (hashValue == null) {
                    hashValue = new HashSet<>();
                    stringSetHashMap.put(key,hashValue);
                }
                hashValue.add(value);
            }
        }
        map.put("spec", stringSetHashMap);

    }

    /**
     * 提取分组聚合结果
     * @param aggregations 聚合结果对象
     * @param group_name 分组域的别名
     * @return 提取的结果集
     */
    private List<String> getGroupResult(Aggregations aggregations, String group_name) {
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get(group_name);
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> specList = new ArrayList<String>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            specList.add(bucket.getKeyAsString());
        }
        return specList;
    }

    /**
     * 分组查询商品分类列表
     * @param builder  查询条件
     * @param map 查询结果集
     */
/*    private void searchCategoryList(NativeSearchQueryBuilder builder, Map map) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_category").field("categoryName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_category");
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> categoryList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            categoryList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("categoryList", categoryList);
    }*/

    /**
     * 分组查询商品品牌分类列表
     * @param builder  查询条件
     * @param map 查询结果集
     */
/*    private void searchBrandList(NativeSearchQueryBuilder builder, Map map) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_brand").field("brandName");
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_brand");
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> brandList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            brandList.add(bucket.getKeyAsString());
        }
        //8.返回分类数据列表-map.put("categoryList", categoryList)
        map.put("brandList", brandList);
    }*/

    /**
     * 分组查询商品规格分类列表
     * @param builder  查询条件
     * @param map 查询结果集
     */
/*    private void searchSpec(NativeSearchQueryBuilder builder, Map map) {
        //1.设置分组域名-termsAggregationBuilder = AggregationBuilders.terms(别名).field(域名);
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_spec").field("spec.keyword").size(1000);
        //2.添加分组查询参数-builder.addAggregation(termsAggregationBuilder)
        builder.addAggregation(termsAggregationBuilder);
        //3.执行搜索-esTemplate.queryForPage(builder.build(), SkuInfo.class)
        AggregatedPage<SkuInfo> page = esTemplate.queryForPage(builder.build(), SkuInfo.class);
        //4.获取所有分组查询结果集-page.getAggregations()
        Aggregations aggregations = page.getAggregations();
        //5.提取分组结果数据-stringTerms = aggregations.get(填入刚才查询时的别名)
        StringTerms stringTerms = aggregations.get("group_spec");
        //6.定义分类名字列表-categoryList = new ArrayList<String>()
        List<String> specList = new ArrayList<>();
        //7.遍历读取分组查询结果-stringTerms.getBuckets().for
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //7.1获取分类名字，并将分类名字存入到集合中-bucket.getKeyAsString()
            specList.add(bucket.getKeyAsString());
        }


        HashMap<String, Set<String>> stringSetHashMap = new HashMap<>();

        for (String spec : specList) {
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                String value = specMap.get(key);
                Set<String> hashValue = stringSetHashMap.get(key);
                if (hashValue == null) {
                    hashValue = new HashSet<>();
                    stringSetHashMap.put(key,hashValue);
                }
                    hashValue.add(value);
            }
        }
        map.put("spec", stringSetHashMap);
    }*/
}
