# 创建、更新和删除文档

&nbsp;&nbsp;&nbsp;&nbsp;使用一款数据库，最主要的就是对齐进行增删改查，接下来就介绍如何在MongoDB中进行数据的新增、删除和修改，以及这些操作在MongoDB中的特点。

## 插入并保存文档

&nbsp;&nbsp;&nbsp;&nbsp;通过使用`insert`命令可以向一个集合中插入新的文档，对于新增的文档，MongoDB客户端会生成一个`_id`。

```sh
> item = {"name":"murdock", "age":18}
> db.foo.insert(item)
> show collections
author_test_collection
blog_test_collection
foo
mongo_task_instance_collection
mongo_test_collecion
mongo_test_collection
```

&nbsp;&nbsp;&nbsp;&nbsp;首先定义了一个对象`item`，随后通过`insert`命令将其插入到集合`foo`中。

> 如果集合不存在，MongoDB会新建立一个集合。通过`show collections`可以查看当前数据库中的所有集合。

&nbsp;&nbsp;&nbsp;&nbsp;在Java中，我们可以使用 **spring-data** 来进行文档的插入。

```java
@Test
public void insert() {
    DBObject dbObject = new BasicDBObject();
    dbObject.put("name", "test");
    dbObject.put("age", 18);
    mongoTemplate.getCollection("foo").insert(dbObject);
}
```

&nbsp;&nbsp;&nbsp;&nbsp;通过构建`DBObject`，向其设置值，并将其插入到文档中。`DBObject`类似`Map`。和关系数据库类似，对于插入，MongoDB也提供了批量新增的方式，也可以使用`insert`方法进行。

```sh
> items = [{"name":"murdock", "age":19}, {"name":"john", "age":20}]
[
	{
		"name" : "murdock",
		"age" : 19
	},
	{
		"name" : "john",
		"age" : 20
	}
]
> db.foo.insert(items)
BulkWriteResult({
	"writeErrors" : [ ],
	"writeConcernErrors" : [ ],
	"nInserted" : 2,
	"nUpserted" : 0,
	"nMatched" : 0,
	"nModified" : 0,
	"nRemoved" : 0,
	"upserted" : [ ]
})
> db.foo.find()
{ "_id" : ObjectId("5cfcf12a6c1c3f471c95190c"), "name" : "murdock", "age" : 18 }
{ "_id" : ObjectId("5cfcfba16c1c3f471c95190d"), "name" : "murdock", "age" : 19 }
{ "_id" : ObjectId("5cfcfba16c1c3f471c95190e"), "name" : "john", "age" : 20 }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到对于`insert`方法，传入了一个数组，MongoDB可以按照数组内容进行插入，好处在于一次通信交互，可以新增多个文档。

> 和MongoDB之间的消息长度不能超过16MB，所以对于批量新增，也需要做好单次容量的估算。
> 
> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.InsertTest`

