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

## 唯一索引

## 执行计划与Hint

## 索引管理

## 地理空间索引

