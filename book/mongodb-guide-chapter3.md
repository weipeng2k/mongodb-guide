# 创建、更新和删除文档

&nbsp;&nbsp;&nbsp;&nbsp;使用一款数据库，最主要的就是对其（关系或集合）进行增删改查，接下来就介绍如何在MongoDB中进行数据的新增、删除和修改，以及这些操作在MongoDB中的特点。

## 插入文档

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

> 如果集合不存在，MongoDB会新建立一个集合。
> 
> 通过`show collections`可以查看当前数据库中的所有集合。

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

&nbsp;&nbsp;&nbsp;&nbsp;这里使用的是MongoDB的Java驱动，当进行`insert`时，如果没有对应的集合时，MongoDB就会创建对应的集合。前面提到`_id`字段，这个是每个文档都会具备的，而例子中进行插入的数据，没有设置`_id`字段。MongoDB会检查插入的文档，当文档中没有包含`_id`，MongoDB客户端会生成一个。

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

## 更新文档

&nbsp;&nbsp;&nbsp;&nbsp;通过使用`update`命令，可以完成对集合中文档的修改动作。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d0dfb1becfa8c49ce3fb2b5"), "name" : "test", "age" : 18 }
> var f = {"name" : "test", "age" : 18}
> f
{ "name" : "test", "age" : 18 }
> f.level = "high"
high
> f
{ "name" : "test", "age" : 18, "level" : "high" }
> db.foo.update({"name":"test"}, f)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5d0dfb1becfa8c49ce3fb2b5"), "name" : "test", "age" : 18, "level" : "high" }
```

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.UpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject dbObject = new BasicDBObject();
dbObject.put("name", "test");
dbObject.put("level", "high");
dbObject.put("age", 20);

DBObject query = new BasicDBObject();
query.put("name", "test");

WriteResult result = mongoTemplate.getCollection("foo").update(query, dbObject);
System.out.println(result.getN() >= 1);
```

&nbsp;&nbsp;&nbsp;&nbsp; 以上的修改方式，属于覆盖式修改，但是我们对于文档的修改，往往是集中在某个属性。针对部分属性的修改，可以使用以下方式。

### $SET

&nbsp;&nbsp;&nbsp;&nbsp;由于默认的Update是全部覆盖，当需要为指定的文档添加一个字段，就需要使用`set`进行操作。

```sh
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bb09124b8e520faedc0aa"),
	"name" : "test",
	"age" : 18,
	"sex" : "male"
}
> db.foo.update({"name":"test"}, {"$set":{"sex":"female"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bb09124b8e520faedc0aa"),
	"name" : "test",
	"age" : 18,
	"sex" : "female"
}
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到使用`$set`，将`sex`属性从male改为了female。

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.UpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject update = new BasicDBObject();
DBObject prop = new BasicDBObject();

prop.put("sex", "male");
update.put("$set", prop);
mongoTemplate.getCollection("foo").update(query, update);
```

&nbsp;&nbsp;&nbsp;&nbsp;使用`$set`进行更新，当文档没有对应的key时会添加，有的话，则修改。

### $INC

&nbsp;&nbsp;&nbsp;&nbsp;针对文档中的数字类型，可以使用增加`inc`进行操作，将一个文档中的数字属性进行增加或者减少（负数）。

```sh
> db.foo.findOne()
{
	"_id" : ObjectId("5d0e0dceecfa8c49ce3fb2b7"),
	"name" : "test",
	"age" : 18,
	"level" : "high"
}
> db.foo.update({"name":"test"} , {"$inc":{"age":1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.update({"name":"test"} , {"$inc":{"age":1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d0e0dceecfa8c49ce3fb2b7"),
	"name" : "test",
	"age" : 20,
	"level" : "high"
}

```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到使用`$inc`，将`age`从18变为20。

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.UpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject update = new BasicDBObject();
DBObject prop = new BasicDBObject();
prop.put("age", 1);
update.put("$inc", prop);

mongoTemplate.getCollection("foo").update(query, update);
```

&nbsp;&nbsp;&nbsp;&nbsp;使用`$inc`进行更新，能够保证当前文档对应的属性更新是原子化的。

## 更新文档，数组属性

### $PUSH 

&nbsp;&nbsp;&nbsp;&nbsp;MongoDB支持的数据类型除了：整型、浮点型、日期以及字符串这种单值类型的之外，还有数组类型。如果对于一个文档中的数组属性进行操作时，可以使用`push`进行操作。

> 该操作只能针对数组属性

```sh
> db.foo.update({"name":"test"}, {"$push":{"hobbies":"reading"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bb09124b8e520faedc0aa"),
	"name" : "test",
	"age" : 18,
	"sex" : "female",
	"hobbies" : [
		"reading"
	]
}
> db.foo.update({"name":"test"}, {"$push":{"hobbies":"tvgaming"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bb09124b8e520faedc0aa"),
	"name" : "test",
	"age" : 18,
	"sex" : "female",
	"hobbies" : [
		"reading",
		"tvgaming"
	]
}
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过使用`push`将一个元素添加到对应的数组的尾部。

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.ArrayUpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject update = new BasicDBObject();
DBObject prop = new BasicDBObject();

prop.put("hobbies", "reading");
update.put("$push", prop);
mongoTemplate.getCollection("foo").update(query, update);
```

&nbsp;&nbsp;&nbsp;&nbsp;使用`$push`进行数组元素的更新，当文档没有对应的数组key时会添加，并将元素添加到数组的尾部。

### $ADDTOSET

&nbsp;&nbsp;&nbsp;&nbsp;使用`push`命令可以向一个文档中的数组属性里添加内容，但是如果该内容已经在数组中出现，而本次添加是当元素在数组中不存在时才生效时，就需要使用`addToSet`。

```sh
> db.foo.update({"name":"test"}, {"$addToSet":{"hobbies":"football"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bc02d24b8e52357933ed0"),
	"name" : "test",
	"age" : 18,
	"hobbies" : [
		"reading",
		"tvgaming",
		"football"
	]
}
> db.foo.update({"name":"test"}, {"$addToSet":{"hobbies":"football"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 0 })
> db.foo.findOne()
{
	"_id" : ObjectId("5d6bc02d24b8e52357933ed0"),
	"name" : "test",
	"age" : 18,
	"hobbies" : [
		"reading",
		"tvgaming",
		"football"
	]
}
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过使用`addToSet`将一个元素以集合Set的视角添加入一个数组，当数组中存在该元素的时候，不会添加。

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.ArrayUpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject update = new BasicDBObject();
DBObject prop = new BasicDBObject();

prop.put("hobbies", "reading");
update.put("$addToSet", prop);
mongoTemplate.getCollection("foo").update(query, update);
```

### $POP

&nbsp;&nbsp;&nbsp;&nbsp;使用`pop`命令可以将一个文档中的数组属性里移出元素，就好比栈一样，调用`pop`就会将栈顶元素移出。可以使用`{"$pop":{key:-1}}`从尾部移出元素。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d6bc02d24b8e52357933ed0"), "name" : "test", "age" : 18, "hobbies" : [ "reading", "tvgaming", "football", "tvgaming", "basketball" ] }
> db.foo.update({"name":"test"}, {"$pop":{"hobbies":-1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5d6bc02d24b8e52357933ed0"), "name" : "test", "age" : 18, "hobbies" : [ "tvgaming", "football", "tvgaming", "basketball" ] }
> db.foo.update({"name":"test"}, {"$pop":{"hobbies":1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5d6bc02d24b8e52357933ed0"), "name" : "test", "age" : 18, "hobbies" : [ "tvgaming", "football", "tvgaming" ] }
> db.foo.update({"name":"test"}, {"$pop":{"hobbies":-1}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5d6bc02d24b8e52357933ed0"), "name" : "test", "age" : 18, "hobbies" : [ "football", "tvgaming" ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过使用`pop`将一个数组中的一个元素移除。

> 对于Java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.ArrayUpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject pop = new BasicDBObject();
DBObject popKey = new BasicDBObject();
popKey.put("hobbies", -1);
pop.put("$pop", popKey);
mongoTemplate.getCollection("foo").update(query, pop);
```

> 也可以通过`pull`命名，指定的移出某个数组中的元素。

## 更新文档，高阶特性

### $UPSERT

&nbsp;&nbsp;&nbsp;&nbsp;在进行数据插入时，经常会遇到一个问题，就是如果主键存在则获取已经存在的数据，否则插入，每当这时候我们需要谨慎的处理，需要增加主键的约束，处理回滚的事务。MongoDB提供了`upsert`命令用来完成这个工作，且该命令是原子性的。

```sh
> db.foo.update({"name": "test"}, {"$inc": {"age": 1}}, true)
WriteResult({
	"nMatched" : 0,
	"nUpserted" : 1,
	"nModified" : 0,
	"_id" : ObjectId("5d9a89e540eabd2b62ced5e9")
})
> db.foo.find()
{ "_id" : ObjectId("5d9a89e540eabd2b62ced5e9"), "name" : "test", "age" : 1 }
> db.foo.update({"name": "test"}, {"$inc": {"age": 1}}, true)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.foo.find()
{ "_id" : ObjectId("5d9a89e540eabd2b62ced5e9"), "name" : "test", "age" : 2 }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到通过在`update`的第三个参数传入`true`，可以将一个修改的命令变成一个`upsert`命令。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.AdvancedUpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();
query.put("name", "test");

DBObject update = new BasicDBObject();
DBObject age = new BasicDBObject();
age.put("age", 1);
update.put("$inc", age);

WriteResult update1 = collection.update(query, update, true, false);
```

&nbsp;&nbsp;&nbsp;&nbsp;如果多次通过`update`进行更新，可以看到第一次更新是进行数据的插入，而后续的更新都是针对该文档的更新。

### FindAndModify

&nbsp;&nbsp;&nbsp;&nbsp;当我们遇到并发更新一行数据时，在使用关系数据库时，经常使用乐观锁进行控制。比如：定义一个字段`version`，当更新时，需要传入查询出的最新`version`，也只有`version`和对应行记录中的`version`一致时，方可更新。这样是可以完成对于一行的并发更新，问题就是需要将这个`version`定义在每个需要并发更新的数据结构中。

&nbsp;&nbsp;&nbsp;&nbsp;这种单行（文档）的并发更新，是一种常见的更新场景，MongoDB对于这种更新场景提供了更加简单的使用方式。

```sh
> db.runCommand({
... "findAndModify":"foo",
... "query":{"name": "test"},
... "update":{"$set":{"age":30}}
... })
{
	"lastErrorObject" : {
		"n" : 1,
		"updatedExisting" : true
	},
	"value" : {
		"_id" : ObjectId("5d9aa2d640eabd2b62ced639"),
		"name" : "test",
		"age" : 5
	},
	"ok" : 1
}
> db.foo.findOne()
{
	"_id" : ObjectId("5d9aa2d640eabd2b62ced639"),
	"name" : "test",
	"age" : 30
}
```

&nbsp;&nbsp;&nbsp;&nbsp;`findAndModify`命令需要输入集合等相关的更新信息，对应的键和类型如下：

|键|类型|描述|
|----|----|-----|
|findAndModify|字符串|对应的集合名|
|query|文档|查询的文档，用来检索|
|sort|文档|排序的文档，用来对检索的结果进行排序|
|update|文档|修改的文档，用来修改|
|remove|布尔|是否删除文档|
|new|布尔|是否返回更新后的文档|

&nbsp;&nbsp;&nbsp;&nbsp;`findAndModify`只能对已经存在的文档进行更新，而且一次只能更新一行。相比较而言`findAndModify`的耗时相当于一次查询、一次更新和执行一次`getLastError`所需的时间。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter3.AdvancedUpdateTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行更新文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();
query.put("name", "test");

DBObject update = new BasicDBObject();
DBObject age = new BasicDBObject();
age.put("age", 20);
update.put("$set", age);

DBObject andModify = collection.findAndModify(query, null, null, false, update, true, false);
```
