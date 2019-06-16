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

### 插入的原理

&nbsp;&nbsp;&nbsp;&nbsp;在`InsertTest`中可以使用如下代码对`foo`集合中进行插入。

```java
@Test
public void insert() {
    DBObject dbObject = new BasicDBObject();
    dbObject.put("name", "test");
    dbObject.put("age", 18);
    mongoTemplate.getCollection("foo").insert(dbObject);
}
```

&nbsp;&nbsp;&nbsp;&nbsp;这里使用的是MongoDB的Java驱动，当进行insert时，如果没有对应的集合时，MongoDB就会创建对应的集合。前面提到`_id`字段，这个是每个文档都会具备的，而例子中进行插入的数据，没有设置`_id`字段。MongoDB会检查插入的文档，当文档中没有包含`_id`，MongoDB客户端会生成一个。

```java
public WriteResult insert(final List<? extends DBObject> documents, final InsertOptions insertOptions) {
    WriteConcern writeConcern = insertOptions.getWriteConcern() != null ? insertOptions.getWriteConcern() : getWriteConcern();
    Encoder<DBObject> encoder = toEncoder(insertOptions.getDbEncoder());

    List<InsertRequest> insertRequestList = new ArrayList<InsertRequest>(documents.size());
    for (DBObject cur : documents) {
        if (cur.get(ID_FIELD_NAME) == null) {
            cur.put(ID_FIELD_NAME, new ObjectId());
        }
        insertRequestList.add(new InsertRequest(new BsonDocumentWrapper<DBObject>(cur, encoder)));
    }
    return insert(insertRequestList, writeConcern, insertOptions.isContinueOnError(), insertOptions.getBypassDocumentValidation());
}
```

> 见`com.mongodb.DBCollection#insert(java.util.List<? extends com.mongodb.DBObject>, com.mongodb.InsertOptions)`

&nbsp;&nbsp;&nbsp;&nbsp;可以看到，文档中没有`_id`属性，MongoDB客户端就会生成一个。可以看一下`ObjectId`的构造函数，以及相关的处理逻辑。

```java
buffer.put(int3(timestamp));
buffer.put(int2(timestamp));
buffer.put(int1(timestamp));
buffer.put(int0(timestamp));
buffer.put(int2(machineIdentifier));
buffer.put(int1(machineIdentifier));
buffer.put(int0(machineIdentifier));
buffer.put(short1(processIdentifier));
buffer.put(short0(processIdentifier));
buffer.put(int2(counter));
buffer.put(int1(counter));
buffer.put(int0(counter));
```

&nbsp;&nbsp;&nbsp;&nbsp;对于默认的`ObjectId`的生成，一个12bytes的主键，前4个byte是时间戳，后三个是机器标示（一般是网卡相关的信息），接下来是进程标示，最后是随机生成的3个byte。

> 由于timestamp的单位是秒，那么在一个进程中进行区分的就是counter部分的3个byte，而3个byte的数据内容是：16777216，一千六百多万。可以说在一秒内，对于一个进程内容，如果不超过一千六百万的TPS，就不会有问题。事实上，现实中不会有这么高的TPS，因为单进程这么高的TPS已经让网卡都被打满。

## 删除文档

&nbsp;&nbsp;&nbsp;&nbsp;通过使用`remove`命令，可以完成对集合中文档的删除动作。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d0638e1afe8102aeab43b52"), "name" : "test", "age" : 18 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b53"), "name" : "test", "age" : 19 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b54"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b55"), "name" : "test", "age" : 21 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b56"), "name" : "test", "age" : 22 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b57"), "name" : "test", "age" : 23 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b58"), "name" : "test", "age" : 24 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b59"), "name" : "test", "age" : 25 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b5a"), "name" : "test", "age" : 26 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b5b"), "name" : "test", "age" : 27 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b5c"), "name" : "test", "age" : 28 }
{ "_id" : ObjectId("5d0638e1afe8102aeab43b5d"), "name" : "test", "age" : 29 }
> db.foo.remove({"age" : {"$gte":19}})
WriteResult({ "nRemoved" : 11 })
> db.foo.find()
{ "_id" : ObjectId("5d0638e1afe8102aeab43b52"), "name" : "test", "age" : 18 }
```

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.RemoveTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行删除文档的关键逻辑如下：

```java
DBObject ageGte = new BasicDBObject();
ageGte.put("$gte", 19);

DBObject removeQuery = new BasicDBObject();
removeQuery.put("age", ageGte);

WriteResult writeResult = mongoTemplate.getCollection("foo").remove(removeQuery);
System.out.println("remove:" + writeResult.getN());
Assert.assertTrue(writeResult.getN() >= 11);
System.out.println("count:" + mongoTemplate.getCollection("foo").count());
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过`DBObject`可以构造出`remove`方法需要的参数，其中`$gte`是一个操作，代表大于等于一个值。