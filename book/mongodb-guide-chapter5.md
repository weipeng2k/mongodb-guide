# 索引

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;索引如同字典的目录一样，是用来加速查询的。具有正确索引的查询会比没有索引的查询快几个数量级，当随着数据量级变大时，愈发明显。

## 索引简介

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;首先我们准备一个集合`author_test_collection`，集合中的文档主要包括了，以下几个字段：`name`和`age`，其中`name`的类型是字符串，而`age`是整型。

> 记住，任何文档都会包含一个`_id`字段

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;集合中包含了100万个元素。

```sh
> db.author_test_collection.count();
1000000
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查找`name`为`Author-3000`的文档，通过`explain`命令，可以输出执行计划。

```sh
> db.author_test_collection.find({"name":"Author-3000"}).explain("executionStats")
{
	"queryPlanner" : {
		"plannerVersion" : 1,
		"namespace" : "test.author_test_collection",
		"indexFilterSet" : false,
		"parsedQuery" : {
			"name" : {
				"$eq" : "Author-3000"
			}
		},
		"winningPlan" : {
			"stage" : "COLLSCAN",
			"filter" : {
				"name" : {
					"$eq" : "Author-3000"
				}
			},
			"direction" : "forward"
		},
		"rejectedPlans" : [ ]
	},
	"executionStats" : {
		"executionSuccess" : true,
		"nReturned" : 1,
		"executionTimeMillis" : 652,
		"totalKeysExamined" : 0,
		"totalDocsExamined" : 1000000,
		"executionStages" : {
			"stage" : "COLLSCAN",
			"filter" : {
				"name" : {
					"$eq" : "Author-3000"
				}
			},
			"nReturned" : 1,
			"executionTimeMillisEstimate" : 570,
			"works" : 1000002,
			"advanced" : 1,
			"needTime" : 1000000,
			"needYield" : 0,
			"saveState" : 7812,
			"restoreState" : 7812,
			"isEOF" : 1,
			"invalidates" : 0,
			"direction" : "forward",
			"docsExamined" : 1000000
		}
	},
	"serverInfo" : {
		"host" : "c40e51fb2caa",
		"port" : 27017,
		"version" : "3.6.9",
		"gitVersion" : "167861a164723168adfaaa866f310cb94010428f"
	},
	"ok" : 1
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们先不用关注执行计划中输出的内容细节，看几个主要的数据指标。

|问题|分析|
|----|----|
|查询走全表扫描|stage的描述是`COLLSCAN`，这个表示全表扫描|
|执行耗时比较长|`executionTimeMillis`执行耗时在652毫秒，这个已经很长了|
|工作开销很大|`works`是MongoDB将操作分解为更细力度的操作单元，这里要耗费1000002个|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过使用`ensureIndex`可以创建集合的索引，这里根据`name`属性创建对应的索引：

```sh
> db.author_test_collection.ensureIndex({"name": 1})
{
	"createdCollectionAutomatically" : false,
	"numIndexesBefore" : 1,
	"numIndexesAfter" : 2,
	"ok" : 1
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`ensureIndex`会在指定的key上建立索引，同时第二个参数表示该索引的方向，其中`1`表示升序，而`-1`表示降序。对于大多数查询场景来说，区别不大，因为对于索引的执行总是从中间开始，但是升序会保证最开始的数据会在内存中，这是一个细微的差别。

> MongoDB的索引和MySQL索引类似，都是左匹配。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用`getIndexes`命令，可以查看当前集合上的索引情况。

```sh
> db.author_test_collection.getIndexes()
[
	{
		"v" : 2,
		"key" : {
			"_id" : 1
		},
		"name" : "_id_",
		"ns" : "test.author_test_collection"
	},
	{
		"v" : 2,
		"key" : {
			"name" : 1
		},
		"name" : "name_1",
		"ns" : "test.author_test_collection"
	}
]
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到，集合`author_test_collection`上有两个索引，其中`name_1`是新添加的。新增索引后，再次执行先前的执行计划，看一下效果。

```sh
> db.author_test_collection.find({"name":"Author-3000"}).explain("executionStats")
{
	"queryPlanner" : {
		"plannerVersion" : 1,
		"namespace" : "test.author_test_collection",
		"indexFilterSet" : false,
		"parsedQuery" : {
			"name" : {
				"$eq" : "Author-3000"
			}
		},
		"winningPlan" : {
			"stage" : "FETCH",
			"inputStage" : {
				"stage" : "IXSCAN",
				"keyPattern" : {
					"name" : 1
				},
				"indexName" : "name_1",
				"isMultiKey" : false,
				"multiKeyPaths" : {
					"name" : [ ]
				},
				"isUnique" : false,
				"isSparse" : false,
				"isPartial" : false,
				"indexVersion" : 2,
				"direction" : "forward",
				"indexBounds" : {
					"name" : [
						"[\"Author-3000\", \"Author-3000\"]"
					]
				}
			}
		},
		"rejectedPlans" : [ ]
	},
	"executionStats" : {
		"executionSuccess" : true,
		"nReturned" : 1,
		"executionTimeMillis" : 1,
		"totalKeysExamined" : 1,
		"totalDocsExamined" : 1,
		"executionStages" : {
			"stage" : "FETCH",
			"nReturned" : 1,
			"executionTimeMillisEstimate" : 0,
			"works" : 2,
			"advanced" : 1,
			"needTime" : 0,
			"needYield" : 0,
			"saveState" : 0,
			"restoreState" : 0,
			"isEOF" : 1,
			"invalidates" : 0,
			"docsExamined" : 1,
			"alreadyHasObj" : 0,
			"inputStage" : {
				"stage" : "IXSCAN",
				"nReturned" : 1,
				"executionTimeMillisEstimate" : 0,
				"works" : 2,
				"advanced" : 1,
				"needTime" : 0,
				"needYield" : 0,
				"saveState" : 0,
				"restoreState" : 0,
				"isEOF" : 1,
				"invalidates" : 0,
				"keyPattern" : {
					"name" : 1
				},
				"indexName" : "name_1",
				"isMultiKey" : false,
				"multiKeyPaths" : {
					"name" : [ ]
				},
				"isUnique" : false,
				"isSparse" : false,
				"isPartial" : false,
				"indexVersion" : 2,
				"direction" : "forward",
				"indexBounds" : {
					"name" : [
						"[\"Author-3000\", \"Author-3000\"]"
					]
				},
				"keysExamined" : 1,
				"seeks" : 1,
				"dupsTested" : 0,
				"dupsDropped" : 0,
				"seenInvalidated" : 0
			}
		}
	},
	"serverInfo" : {
		"host" : "c40e51fb2caa",
		"port" : 27017,
		"version" : "3.6.9",
		"gitVersion" : "167861a164723168adfaaa866f310cb94010428f"
	},
	"ok" : 1
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到`stage`已经从 **COLLSCAN** 变为了 **IXSCAN**，也就是说从全表扫描变为了按照索引扫描。有了索引，就好比查字典时有了字母的顺序，使得不用翻遍整个字典才能找到需要的内容，工作量也会减少。可以看到`work`只有4`(2 + 2)`，从1000002下降到4，是非常显著的，相差了百万个量级。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;但是并不是我们将需要查询的字段都放到索引中就可以，有时我们的查询需要考虑顺序，比如：

```sh
db.author_test_collection.find({"name":"Author-2000"}).sort({"age":1})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;该查询用来查询名称为`Author-2000`的所有作者，同时按照`age`正序（小到大）排序返回。可以输出以下执行计划（重点部分）。

```sh
"executionStages" : {
			"stage" : "SORT",
			"nReturned" : 1,
			"executionTimeMillisEstimate" : 0,
			"works" : 5,
			"advanced" : 1,
			"needTime" : 3,
			"needYield" : 0,
			"saveState" : 0,
			"restoreState" : 0,
			"isEOF" : 1,
			"invalidates" : 0,
			"sortPattern" : {
				"age" : 1
			},
			"memUsage" : 118,
			"memLimit" : 33554432,
			"inputStage" : {
				"stage" : "SORT_KEY_GENERATOR",
				"nReturned" : 1,
				"executionTimeMillisEstimate" : 0,
				"works" : 3,
				"advanced" : 1,
				"needTime" : 1,
				"needYield" : 0,
				"saveState" : 0,
				"restoreState" : 0,
				"isEOF" : 1,
				"invalidates" : 0,
				"inputStage" : {
					"stage" : "FETCH",
					"nReturned" : 1,
					"executionTimeMillisEstimate" : 0,
					"works" : 2,
					"advanced" : 1,
					"needTime" : 0,
					"needYield" : 0,
					"saveState" : 0,
					"restoreState" : 0,
					"isEOF" : 1,
					"invalidates" : 0,
					"docsExamined" : 1,
					"alreadyHasObj" : 0,
					"inputStage" : {
						"stage" : "IXSCAN",
						"nReturned" : 1,
						"executionTimeMillisEstimate" : 0,
						"works" : 2,
						"advanced" : 1,
						"needTime" : 0,
						"needYield" : 0,
						"saveState" : 0,
						"restoreState" : 0,
						"isEOF" : 1,
						"invalidates" : 0,
						"keyPattern" : {
							"name" : 1
						},
						"indexName" : "name_1",
						"isMultiKey" : false,
						"multiKeyPaths" : {
							"name" : [ ]
						},
						"isUnique" : false,
						"isSparse" : false,
						"isPartial" : false,
						"indexVersion" : 2,
						"direction" : "forward",
						"indexBounds" : {
							"name" : [
								"[\"Author-2000\", \"Author-2000\"]"
							]
						},
						"keysExamined" : 1,
						"seeks" : 1,
						"dupsTested" : 0,
						"dupsDropped" : 0,
						"seenInvalidated" : 0
					}
				}
			}
		},
		"allPlansExecution" : [ ]
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到首先查询的数据会消耗2个`work`通过`IXSCAN`索引扫描命中数据，然后经过`FETCH`阶段，消耗2个`work`将数据取出，取出的数据需要经历`SORT_KEY_GENERATOR`消耗1个`work`进行排序器的生成（虽然只有一个数据），最终通过`SORT`阶段消耗5个`work`将数据完成排序后返回。

> 总计开销是10个`work`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对于数据的查询输入，有`name`和`age`两个参数，在`age`上有排序，下面在`name`和`age`上建立索引。

```sh
db.author_test_collection.ensureIndex({"name":1, "age":1})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;然后再次执行，可以看到执行计划有所变化。

```sh
"executionStages" : {
			"stage" : "FETCH",
			"nReturned" : 1,
			"executionTimeMillisEstimate" : 0,
			"works" : 3,
			"advanced" : 1,
			"needTime" : 0,
			"needYield" : 0,
			"saveState" : 0,
			"restoreState" : 0,
			"isEOF" : 1,
			"invalidates" : 0,
			"docsExamined" : 1,
			"alreadyHasObj" : 0,
			"inputStage" : {
				"stage" : "IXSCAN",
				"nReturned" : 1,
				"executionTimeMillisEstimate" : 0,
				"works" : 2,
				"advanced" : 1,
				"needTime" : 0,
				"needYield" : 0,
				"saveState" : 0,
				"restoreState" : 0,
				"isEOF" : 1,
				"invalidates" : 0,
				"keyPattern" : {
					"name" : 1,
					"age" : 1
				},
				"indexName" : "name_1_age_1",
				"isMultiKey" : false,
				"multiKeyPaths" : {
					"name" : [ ],
					"age" : [ ]
				},
				"isUnique" : false,
				"isSparse" : false,
				"isPartial" : false,
				"indexVersion" : 2,
				"direction" : "forward",
				"indexBounds" : {
					"name" : [
						"[\"Author-2000\", \"Author-2000\"]"
					],
					"age" : [
						"[MinKey, MaxKey]"
					]
				},
				"keysExamined" : 1,
				"seeks" : 1,
				"dupsTested" : 0,
				"dupsDropped" : 0,
				"seenInvalidated" : 0
			}
		}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到首先消耗2个`work`根据`name_1_age_1`的索引就可以拿到一个指定`name`且`age`排好序的结果，然后将其通过`FETCH`阶段，消耗3个`work`将数据返回即可。整个消耗`work`只有原来的一半。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在建立索引时，不仅要考虑输入的场景字段，还要考虑会在哪些字段上排序。

> 每个集合上的索引默认最大个数是64个，一般来说是够用了。

## 唯一索引

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过建立索引，可以让查询变得更加有效率，这里提到的索引和MySQL中的索引是一个含义。在关系数据库中，还有一种索引称之为唯一索引（Unique），它指的是这个索引能够唯一的确定一条记录，并且能够用这个约束来约束集合中的所有数据。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在MongoDB中，每个集合天生就有一个唯一索引，`_id`，它表示一行数据的id。使用者也可以创建自己的唯一索引，例如：

```sh
db.author_test_collection.ensureIndex({"name":1, "age":1}, {"unique":true})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过创建索引时，增加`{"unique":true}`，可以将该索引设置为唯一索引。

## 执行计划与Hint

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以如下执行计划为例，进行执行计划的内容分析。

```json
{
	"queryPlanner" : {
		"plannerVersion" : 1,
		"namespace" : "test.author_test_collection",
		"indexFilterSet" : false,
		"parsedQuery" : {
			"name" : {
				"$eq" : "Author-2000"
			}
		},
		"winningPlan" : {
			"stage" : "FETCH",
			"inputStage" : {
				"stage" : "IXSCAN",
				"keyPattern" : {
					"name" : 1,
					"age" : 1
				},
				"indexName" : "name_1_age_1",
				"isMultiKey" : false,
				"multiKeyPaths" : {
					"name" : [ ],
					"age" : [ ]
				},
				"isUnique" : false,
				"isSparse" : false,
				"isPartial" : false,
				"indexVersion" : 2,
				"direction" : "forward",
				"indexBounds" : {
					"name" : [
						"[\"Author-2000\", \"Author-2000\"]"
					],
					"age" : [
						"[MinKey, MaxKey]"
					]
				}
			}
		},
		"rejectedPlans" : [
			
		]
	},
	"executionStats" : {
		"executionSuccess" : true,
		"nReturned" : 1,
		"executionTimeMillis" : 1,
		"totalKeysExamined" : 1,
		"totalDocsExamined" : 1,
		"executionStages" : {
			"stage" : "FETCH",
			"nReturned" : 1,
			"executionTimeMillisEstimate" : 0,
			"works" : 3,
			"advanced" : 1,
			"needTime" : 0,
			"needYield" : 0,
			"saveState" : 0,
			"restoreState" : 0,
			"isEOF" : 1,
			"invalidates" : 0,
			"docsExamined" : 1,
			"alreadyHasObj" : 0,
			"inputStage" : {
				"stage" : "IXSCAN",
				"nReturned" : 1,
				"executionTimeMillisEstimate" : 0,
				"works" : 2,
				"advanced" : 1,
				"needTime" : 0,
				"needYield" : 0,
				"saveState" : 0,
				"restoreState" : 0,
				"isEOF" : 1,
				"invalidates" : 0,
				"keyPattern" : {
					"name" : 1,
					"age" : 1
				},
				"indexName" : "name_1_age_1",
				"isMultiKey" : false,
				"multiKeyPaths" : {
					"name" : [ ],
					"age" : [ ]
				},
				"isUnique" : false,
				"isSparse" : false,
				"isPartial" : false,
				"indexVersion" : 2,
				"direction" : "forward",
				"indexBounds" : {
					"name" : [
						"[\"Author-2000\", \"Author-2000\"]"
					],
					"age" : [
						"[MinKey, MaxKey]"
					]
				},
				"keysExamined" : 1,
				"seeks" : 1,
				"dupsTested" : 0,
				"dupsDropped" : 0,
				"seenInvalidated" : 0
			}
		},
		"allPlansExecution" : [
			
		]
	}
	"ok" : 1
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在执行计划`queryPlanner`中会包含本地选取的执行计划，和被拒绝的执行计划。在`executionStats`中会有详细的执行计划过程说明，从整体上会有以下内容需要注意：

|项目|含义|
|---|----|
|executionStats.executionSuccess|是否执行成功|
|executionStats.nReturned|查询的返回条数|
|executionStats.executionTimeMillis|整体执行时间|
|executionStats.totalKeysExamined|索引扫描次数|
|executionStats.totalDocsExamined|文档扫描次数|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在`executionStages`中有当前查询的不同阶段明细，也就是由这些Stage组成的本次查询，Stage好比阶段，一次查询由多个阶段来完成，阶段主要分为以下内容，它们负责不同的工作。

|阶段名称|含义|
|---|----|
|COLLSCAN|全表扫描|
|IXSCAN|索引扫描|
|FETCH|根据索引去检索指定文档，有点回表的感觉|
|SORT|表明在内存中进行了排序，在开始未加age索引时有该阶段|
|LIMIT|使用limit限制返回数|
|SKIP|使用skip进行跳过|
|IDHACK|针对_id进行查询|
|COUNT|count()之类进行count运算|
|PROJECTION|限定返回字段时候stage的返回|
|SHARDING_FILTER|通过mongos对分片数据进行查询|
|SHARD_MERGE|将各个分片返回数据进行merge|

## 索引管理

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;单个索引的名称长度一般不能超过121个字节，也就是60个字符，这个很容易做到。在之前建立索引时，只罗列了字段名，所以默认的索引名称都是`字段`+`索引方向`构成，可以在创建索引的时候，给一个好理解的名称。

```sh
db.author_test_collection.ensureIndex({"name":1, "age":1}, {"name":"index_name"})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在创建索引时，可以在第二个参数传递索引的名字。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;索引的建立需要消耗不少资源，当索引建立时，mongodb对于请求不能够立即响应，因此建立索引的时间需要放在访问量较低的时候，同时也不能够停机进行索引建立。可以通过增加`{"background": true}`参数，例如：

```sh
db.author_test_collection.ensureIndex({"name":1, "age":1}, {"name":"index_name"}, {"background": true})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这时，索引建立会在后台慢慢完成，MongoDB能够处理请求的同时完成索引在后台建立，虽然慢了些，但是不会阻塞请求。

## 地理空间索引

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB支持空间索引，因此可以方便的完成基于地理坐标的查询和搜索。我们定义一个Poi对象：

```java
public class Poi implements Serializable {
    private static final long serialVersionUID = -6595624172489524435L;
    /**
     * ID
     */
    private Long num;
    /**
     * 名称
     */
    private String name;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 地区
     */
    private String areaName;
    /**
     * 街道
     */
    private String streetName;
    /**
     * 位置
     */
    private Double[] location;
    /**
     * 类目名
     */
    private String categoryName;
    /**
     * 属性名
     */
    private String propertyName;
    /**
     * 联系方式
     */
	private String[] contactNumbers;
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;该对象声明了地理位置的名称，市、区和街道地址，以及地理位置（经纬度），通过使用以下测试将数据完成初始化。

```java
@Test
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
```

> 上述测试文件和类型定义，可以在项目工程`mongodb-guide-chapter5`中找到。当前数据大约有11万左右的POI信息。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在开始检索之前，需要建立地理位置索引，这里可以用如下命令进行建立：

```sh
db.poi.createIndex({"location":"2d"})
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;建立完成后，使用`MongoTemplate`来查询一下，离笔者家最近的（2公里）的火锅店。

```java
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
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到对应的输出：

```sh
GeoResults: [averageDistance: 1.0906241539249202 org.springframework.data.geo.CustomMetric@10650953, results: GeoResult [content: Poi{num=18631, name='锅sir时尚火锅(西溪店)', cityName='杭州市', areaName='余杭区', streetName='五常街道丰岭路150号', location=[30.2518344901, 120.027799463], categoryName='美食', propertyName='火锅', contactNumbers=[15088631350]}, distance: 0.31474643777909406 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=929494, name='华记肉蟹煲', cityName='杭州市', areaName='余杭区', streetName='五常大道156号', location=[30.2478319766, 120.02888752], categoryName='美食', propertyName='火锅', contactNumbers=[13858025848]}, distance: 0.7118123988834714 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=138637, name='贵州闵孔维火锅', cityName='杭州市', areaName='余杭区', streetName='乐玛摄影乐品美学馆南(联胜路东)', location=[30.2481295413, 120.025993642], categoryName='美食', propertyName='火锅', contactNumbers=[15158096376]}, distance: 0.771100041115541 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=154459, name='铜顺祥老北京炭火锅(五常店)', cityName='杭州市', areaName='余杭区', streetName='五常大道169-2号楼达峰科技园对面', location=[30.2464425735, 120.028698033], categoryName='美食', propertyName='火锅', contactNumbers=[13396553333]}, distance: 0.8671627646732913 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=29323, name='重庆老基地主题火锅(杭州总店)', cityName='杭州市', areaName='余杭区', streetName='留下五常街道天目山西路15号宏丰生活广场北面C2-103号', location=[30.240354346, 120.028189614], categoryName='美食', propertyName='火锅', contactNumbers=[0571-88667515]}, distance: 1.5453050765681462 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=300403, name='锅内锅外(西溪印象城店)', cityName='杭州市', areaName='西湖区', streetName='五常大道1号杭州西溪印象城L401', location=[30.249636746, 120.043730212], categoryName='美食', propertyName='火锅', contactNumbers=[0571-85100078]}, distance: 1.6792517080143305 org.springframework.data.geo.CustomMetric@10650953, ],GeoResult [content: Poi{num=313858, name='煮火火锅', cityName='杭州市', areaName='西湖区', streetName='五常大道1号(近西溪湿地公园)西溪印象城L2层L248', location=[30.2495355104, 120.044319313], categoryName='美食', propertyName='火锅', contactNumbers=[0571-88639773]}, distance: 1.7449906504405666 org.springframework.data.geo.CustomMetric@10650953, ]]
GeoResult [content: Poi{num=18631, name='锅sir时尚火锅(西溪店)', cityName='杭州市', areaName='余杭区', streetName='五常街道丰岭路150号', location=[30.2518344901, 120.027799463], categoryName='美食', propertyName='火锅', contactNumbers=[15088631350]}, distance: 0.31474643777909406 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=929494, name='华记肉蟹煲', cityName='杭州市', areaName='余杭区', streetName='五常大道156号', location=[30.2478319766, 120.02888752], categoryName='美食', propertyName='火锅', contactNumbers=[13858025848]}, distance: 0.7118123988834714 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=138637, name='贵州闵孔维火锅', cityName='杭州市', areaName='余杭区', streetName='乐玛摄影乐品美学馆南(联胜路东)', location=[30.2481295413, 120.025993642], categoryName='美食', propertyName='火锅', contactNumbers=[15158096376]}, distance: 0.771100041115541 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=154459, name='铜顺祥老北京炭火锅(五常店)', cityName='杭州市', areaName='余杭区', streetName='五常大道169-2号楼达峰科技园对面', location=[30.2464425735, 120.028698033], categoryName='美食', propertyName='火锅', contactNumbers=[13396553333]}, distance: 0.8671627646732913 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=29323, name='重庆老基地主题火锅(杭州总店)', cityName='杭州市', areaName='余杭区', streetName='留下五常街道天目山西路15号宏丰生活广场北面C2-103号', location=[30.240354346, 120.028189614], categoryName='美食', propertyName='火锅', contactNumbers=[0571-88667515]}, distance: 1.5453050765681462 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=300403, name='锅内锅外(西溪印象城店)', cityName='杭州市', areaName='西湖区', streetName='五常大道1号杭州西溪印象城L401', location=[30.249636746, 120.043730212], categoryName='美食', propertyName='火锅', contactNumbers=[0571-85100078]}, distance: 1.6792517080143305 org.springframework.data.geo.CustomMetric@10650953, ]
GeoResult [content: Poi{num=313858, name='煮火火锅', cityName='杭州市', areaName='西湖区', streetName='五常大道1号(近西溪湿地公园)西溪印象城L2层L248', location=[30.2495355104, 120.044319313], categoryName='美食', propertyName='火锅', contactNumbers=[0571-88639773]}, distance: 1.7449906504405666 org.springframework.data.geo.CustomMetric@10650953, ]
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;返回的结果包含了符合要求的Poi信息，给出了平均的距离，想尝试的同学可以试着写一个找寻自家最近加油站的程序。