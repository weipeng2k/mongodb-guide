package com.murdock.books.mongodbguide.chapter5.domain;

import com.murdock.books.mongodbguide.common.config.MongoConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.data.geo.Metrics.KILOMETERS;

/**
 * @author weipeng2k 2020年03月15日 下午20:15:18
 */
@SpringBootTest(classes = PoiTest.Config.class)
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class PoiTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @Ignore
    public void init() throws Exception {
        Path path = Paths.get("src/test/resources", "hangzhou-2018-poi.csv");
        List<String> allLines = Files.readAllLines(path);

        allLines.parallelStream()
                .map(s -> {
                    String[] split = s.split(",");
                    if (split.length == 8) {
                        try {
                            Long num = Long.parseLong(split[0].trim());
                            String name = split[1].trim();
                            String streetName = split[2].trim();
                            Double longitude = Double.parseDouble(split[3].trim());
                            Double latitude = Double.parseDouble(split[4].trim());
                            String[] address = split[5].split(";");
                            String cityName = null;
                            String areaName = null;
                            if (address.length == 2) {
                                cityName = address[0].trim();
                                areaName = address[1].trim();
                            } else {
                                cityName = address[0].trim();
                            }
                            String[] cate = split[6].split(";");
                            String categoryName = cate[0].trim();
                            String propertyName = cate[1].trim();
                            String[] contacts = split[7].split(";");

                            Poi poi = new Poi();
                            poi.setNum(num);
                            poi.setName(name);
                            poi.setLocation(new Double[]{longitude, latitude});
                            poi.setCityName(cityName);
                            poi.setAreaName(areaName);
                            poi.setStreetName(streetName);
                            poi.setCategoryName(categoryName);
                            poi.setPropertyName(propertyName);
                            poi.setContactNumbers(contacts);
                            return poi;
                        } catch (Exception ex) {
                            System.err.println(s);
                            return null;
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(poi -> mongoTemplate.save(poi, "poi"));
    }

    /**
     * 美食:[西餐, 特色中餐, 自助餐, 冷饮店, 海鲜, 日韩菜, 素食, 其它美食, 烧烤, 私家菜, 火锅, 茶餐厅, 面包甜点, 清真, 小吃快餐, 东南亚菜, 中餐厅, 家常菜, 农家菜]
     * 汽车:[洗车场, 驾校, 汽车租赁, 摩托车, 加油站, 汽车配件销售, 汽车养护, 汽车维修, 二手车交易市场, 其它汽车, 汽车销售, 停车场]
     * 地名地址:[山, 河流, 其它地名地址, 热点区域, 湖泊, 门牌信息, 行政地名, 自然地名]
     * 其它:[其它]
     * 机构团体:[政府机关, 工商税务机构, 科研机构, 外国机构, 传媒机构, 飞机场, 车辆管理机构, 社会团体, 公检法机构, 文艺团体]
     * 旅游服务:[旅行社]
     * 公司企业:[其它机构团体, 公司企业]
     * 购物:[家具家居建材, 体育户外, 礼品, 超市, 烟酒专卖, 摄影器材, 小商品市场, 汽车俱乐部, 商业步行街, 化妆品, 母婴儿童, 其它购物, 珠宝饰品, 图书音像, 拍卖典当行, 农贸市场, 文化用品, 眼镜店, 旧货市场, 古玩字画, 钟表店, 花鸟鱼虫, 自行车专卖, 服饰鞋包, 便利店, 数码家电, 综合商场]
     * 休闲娱乐:[茶馆, 夜总会, 电影院, 酒吧, 游乐场, 度假疗养, 剧场音乐厅, 洗浴推拿足疗, 迪厅, 其它娱乐休闲, KTV, 户外活动, 旅游景点, 咖啡厅, 网吧]
     * 酒店宾馆:[酒店宾馆, 酒店宾馆附属, 经济型酒店, 旅馆招待所, 青年旅社, 星级酒店, 公寓式酒店, 度假村, 其它酒店宾馆]
     * 文化场馆:[博物馆, 科技馆, 档案馆, 展览馆, 会展中心, 其它文化场馆, 文化宫, 美术馆, 图书馆]
     * 教育学校:[中学, 成人教育, 职业技术学校, 大学, 保险公司, 证券公司, 教育学校附属, 其它教育学校, 幼儿园, 小学, 培训]
     * 生活服务:[洗衣店, 婚庆服务, 家政, 电力营业厅, 其它生活服务, 教练, 丧葬, 美容美发, 摄影冲印, 彩票, 通讯服务, 报刊亭, 票务代售, 信息咨询中心, 自来水营业厅, 邮局速递, 中介机构, 废品收购站, 生活服务场所, 招聘求职, 事务所, 福利院养老院, 宠物服务]
     * 交通运输:[港口码头]
     * 基础设施:[地铁站出入口, 火车站附属, 路口, 桥, 其它交通设施, 长途汽车站, 火车站, 其它室内及附属设施, 服务区, 公用电话, 公共厕所, 道路出入口, 地铁站, 道路附属, 门/出入口, 公交车站, 收费站, 机场附属, 道路名]
     * 建筑房产:[商务楼宇, 别墅, 产业园区, 住宅小区, 房产小区附属, 住宅区, 其它房产小区, 宿舍, 社区中心]
     * 银行金融:[其它银行金融, 财务公司, 银行, 自动提款机]
     * 医疗保健:[齿科, 其它医疗保健, 整形, 精神病医院, 诊所, 疾病预防, 妇产科, 综合医院, 宠物医院, 专科医院, 药房药店, 急救中心, 其它专科医院, 骨科, 医疗保健附属]
     * 运动健身:[游泳馆, 高尔夫场, 网球场, 跆拳道, 足球场, 乒乓球馆, 瑜伽, 游戏棋牌, 综合体育场馆, 马术, 健身中心, 羽毛球馆, 保龄球馆, 溜冰, 台球馆, 篮球场, 舞蹈, 其它运动健身]
     */
    @Test
    @Ignore
    public void category() {
        Map<String, Set<String>> category = new HashMap<>();
        CloseableIterator<Poi> poi = mongoTemplate.stream(new Query(), Poi.class, "poi");
        while (poi.hasNext()) {
            Poi next = poi.next();
            Set<String> stringSet = category.computeIfAbsent(next.getCategoryName(), key -> new HashSet<>());
            stringSet.add(next.getPropertyName());
        }

        for (Map.Entry<String, Set<String>> stringSetEntry : category.entrySet()) {
            System.out.println(stringSetEntry.getKey() + ":" + stringSetEntry.getValue());
        }
    }

    @Test
    public void findNearHG() {
        Query query = new Query();
        Criteria category = Criteria.where("categoryName").is("美食");
        Criteria property = Criteria.where("propertyName").is("火锅");
        query.addCriteria(category).addCriteria(property);
        NearQuery nearQuery = NearQuery.near(30.2542303641, 120.029316088)
                .maxDistance(2/111.0d)
                .distanceMultiplier(111)
                .query(query);

        GeoResults<Poi> poi = mongoTemplate.geoNear(nearQuery, Poi.class, "poi");
        System.out.println(poi);
        poi.forEach(System.out::println);
    }

    @Test
    public void findNearGS() {
        Query query = new Query();
        Criteria category = Criteria.where("categoryName").is("汽车");
        Criteria property = Criteria.where("propertyName").is("加油站");
        query.addCriteria(category).addCriteria(property);
        NearQuery nearQuery = NearQuery.near(30.2542303641, 120.029316088)
                .maxDistance(5/111.0d)
                .distanceMultiplier(111)
                .query(query);

        GeoResults<Poi> poi = mongoTemplate.geoNear(nearQuery, Poi.class, "poi");
        System.out.println(poi);
        poi.forEach(System.out::println);

    }

    @Test
    public void findNearSchool() {
        Query query = new Query();
        Criteria category = Criteria.where("categoryName").is("教育学校");
        Criteria property = Criteria.where("propertyName").is("培训");
        query.addCriteria(category).addCriteria(property);
        NearQuery nearQuery = NearQuery.near(30.2542303641, 120.029316088)
                .maxDistance(5/111.0d)
                .distanceMultiplier(111)
                .query(query);

        GeoResults<Poi> poi = mongoTemplate.geoNear(nearQuery, Poi.class, "poi");
        System.out.println(poi);
        poi.forEach(System.out::println);
    }

    @Configuration
    @Import(MongoConfig.class)
    static class Config {

    }
}