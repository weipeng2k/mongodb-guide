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

&nbsp;&nbsp;&nbsp;&nbsp;可以看到查询语句中，先给出了限定的文档字段`age`，随后跟着对字段的限定内容。、

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ConditionFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$gt", 20);

query.put("age", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

### `$IN`

&nbsp;&nbsp;&nbsp;&nbsp;在关系数据库中，可以使用`in`查询来进行单key的多值查询，比如：

```sql
select * from user where age in (18, 19, 20);
```

&nbsp;&nbsp;&nbsp;&nbsp;上述SQL可以查询`age`为18、19或者20的所有`user`，而这种查询方式，MongoDB也有提供，方式和SQL很类似。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
> db.foo.find({"age": {"$in": [20,21]}})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到查询语句中，对于文档属性`name`进行了限定，限定描述是`$in`，同时指定值是`[20, 21]`。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.InFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$in", new int[]{20, 21});

query.put("age", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;`$in`操作符还是用来限定文档的一个属性，而通过`$or`可以连接对于多个属性的限定。

### `$OR`

&nbsp;&nbsp;&nbsp;&nbsp;在关系数据库中，可以通过`or`来限定查询条件，MongoDB也提供了类似的解决方案，也就是使用`$or`操作符。

```sh
> db.foo.find({"$or": [{"name": "test"}, {"age": 21}]})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;上述查询语句会查询集合`foo`中，`name`属性为`test`，`age`属性为`21`的文档。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.OrFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject nameQuery = new BasicDBObject();
nameQuery.put("name", "test");

DBObject ageQuery = new BasicDBObject();
ageQuery.put("age", 21);

query.put("$or", new DBObject[] {nameQuery, ageQuery});

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;`$or`操作符可以连接多个对于不同（或者相同）属性的查询限定，在一定程度上要比`$in`灵活。同理也可以用`$and`操作符来连接多个查询条件。

## 高级查询

&nbsp;&nbsp;&nbsp;&nbsp;前文介绍了基本的查询功能，能够指定集合中文档的键进行精确或者范围查询，上述基本的查询功能也覆盖了大部分关系数据库中的查询方法。这些基本的查询方法对应的是基础数据类型，由于MongoDB提供了数组和子文档，这就使得查询这些特有的数据类型需要更为强大的查询方法，而这些也是MongoDB优于传统关系数据库查询功能的关键。

### 数组查询

&nbsp;&nbsp;&nbsp;&nbsp;数组类型是一组相同基础类型的集合，比如：

```json
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;其中`value`属性存储的内容就是数组，数组元素的类型是INT。

&nbsp;&nbsp;&nbsp;&nbsp;如果查询时，查询条件是数组中的一个元素，则一旦文档中的数组元素包含了这个元素，就会返回。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
{ "_id" : ObjectId("5de3abffc28644d98389fade"), "name" : "hehe", "value" : [ 3, 4, 5 ] }
> db.foo.find({"value": 3})
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
{ "_id" : ObjectId("5de3abffc28644d98389fade"), "name" : "hehe", "value" : [ 3, 4, 5 ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到，`db.foo.find({"value": 3})`可以查询出所有`value`数组属性包含了`3`的文档。

#### 精确匹配`$all`

&nbsp;&nbsp;&nbsp;&nbsp;按照`db.foo.find({"value": 3})`是一种单值的匹配方式，有时我们需要一种使用数组的匹配方式，比如：找出`value`包含了`[1, 2]`两个元素的文档，这就需要使用`$all`操作符了。

```sh
> db.foo.find({"value": {"$all": [1,4]}})
> db.foo.find({"value": {"$all": [1,2]}})
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到当输入的数组元素是`[1, 2]`时，就可以查询到对应的文档。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ArrayFindTest#all`

&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$all", new int[]{1, 2});

query.put("value", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;可以看到针对`value`这个属性，使用的查询限定是`{"$all": [1, 2]}`，表示查询`value`中全部包含了`1`和`2`元素的文档。

## 游标