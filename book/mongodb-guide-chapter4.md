# 查询

&nbsp;&nbsp;&nbsp;&nbsp;对MongoDB进行新增、修改和删除后，最主要的功能就是对数据（集合）进行查询，MongoDB支持丰富的查询功能。

## 查询简介

&nbsp;&nbsp;&nbsp;&nbsp;使用`find`命令可以查询集合中的文档，并返回符合要求的文档子集。查询命令的第一个参数是查询的模式，第二个参数是指定返回的键。查询的模式有些类似JPA中的查询模式，只需要表述需要的模式就行，例如：如果要查询属性`name`为`test`的文档，模式就是：`{"name": "test"}`。返回的键可以通过声明指定的键即可，例如：如果要返回`name`，声明就是：`{"name": 1}`。

&nbsp;&nbsp;&nbsp;&nbsp;如果需要查询`age`为`20`的`name`，查询命令参数就是：`{"age": 20}, {"name": 1}`。

```sh
> db.foo.find({"age": 20}, {"name": 1})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test" }
> db.foo.find({"age": 20}, {"name": 1, "age": 1})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
> db.foo.find({"age": 20}, {"name": 1, "_id": 0})
{ "name" : "test" }
```

> 默认会返回属性`_id`，如果不需要可以声明`{"_id": 0}`

&nbsp;&nbsp;&nbsp;&nbsp;`find`是和MongoDB交互的基础命令，可以通过它来查询集合中的文档（数据）。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.FindTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();
query.put("age", 20);
DBObject project = new BasicDBObject();
project.put("name", 1);
project.put("_id", 0);
DBCursor dbObjects = collection.find(query, project);

dbObjects.forEach(System.out::println);

dbObjects = collection.find(query);
dbObjects.forEach(System.out::println);
```

&nbsp;&nbsp;&nbsp;&nbsp;`Collection.find()`方法的文档描述是：`Select documents in collection and get a cursor to the selected documents.`，可以看到调用`find`方法，返回的是指向符合查询条件要求的文档游标。只要我们获取了游标，我们就可以遍历它，访问符合我们要求的文档了。

> 这里和JDBC有些区别，传统的JDBC通过`Collection`和`Statement`访问RDBMS后，得到的是数据集`RowSet`

## 根据条件查询

&nbsp;&nbsp;&nbsp;&nbsp;通过使用`find`命令，可以查询集合中符合要求的文档，但是我们对于数据库的使用不是只限定在这种简单的操作，还有一些比较通用的查询要求，比如：范围查询等。

### 条件查询

&nbsp;&nbsp;&nbsp;&nbsp;在SQL查询中，可以使用`>`或者`<`来进行范围的控制，也就是比较操作符。在MongoDB中，由于查询的语句都是JSON，所以需要用转移字符来替换掉我们常用的比较操作符。

&nbsp;&nbsp;&nbsp;&nbsp;对应关系如下表：

|类型|描述|
|---|---|
|`$lt`|less than，也就是 <|
|`$lte`|less than or equal，也就是 <=|
|`$gt`|greater than，也就是 >|
|`$gte`|greater than or equal，也就是 >=|
|`$ne`|not equal, 也就是 <>|

&nbsp;&nbsp;&nbsp;&nbsp;根据`age`进行范围查询，先查询`age`小于等于`21`的。

```sh
> db.foo.find();
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
> db.foo.find({"age": {"$lte" : 20}})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
> db.foo.find({"age": {"$gt" : 20}})
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到查询语句中，先给出了限定的文档字段`age`，随后跟着对字段的限定内容。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ConditionFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$gt", 20);

query.put("age", condition);

DBCursor dbObjects = collection.find(query);
```

### $OR

&nbsp;&nbsp;&nbsp;&nbsp;在关系数据库中，可以通过`or`来限定查询条件，MongoDB也提供了类似的解决方案，也就是使用`$or`操作符。


## 高级查询

## 游标