# 查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对MongoDB进行新增、修改和删除后，最主要的功能就是对数据（集合）进行查询，MongoDB支持丰富的查询功能。

## 查询简介

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用`find`命令可以查询集合中的文档，并返回符合要求的文档子集。查询命令的第一个参数是查询的模式，第二个参数是指定返回的键。查询的模式有些类似JPA中的查询模式，只需要表述需要的模式就行，例如：如果要查询属性`name`为`test`的文档，模式就是：`{"name": "test"}`。返回的键可以通过声明指定的键即可，例如：如果要返回`name`，声明就是：`{"name": 1}`。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果需要查询`age`为`20`的`name`，查询命令参数就是：`{"age": 20}, {"name": 1}`。

```sh
> db.foo.find({"age": 20}, {"name": 1})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test" }
> db.foo.find({"age": 20}, {"name": 1, "age": 1})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
> db.foo.find({"age": 20}, {"name": 1, "_id": 0})
{ "name" : "test" }
```

> 默认会返回属性`_id`，如果不需要可以声明`{"_id": 0}`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`find`是和MongoDB交互的基础命令，可以通过它来查询集合中的文档（数据）。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.FindTest`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

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

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`Collection.find()`方法的文档描述是：`Select documents in collection and get a cursor to the selected documents.`，可以看到调用`find`方法，返回的是指向符合查询条件要求的文档游标。只要我们获取了游标，我们就可以遍历它，访问符合我们要求的文档了。

> 这里和JDBC有些区别，传统的JDBC通过`Collection`和`Statement`访问RDBMS后，得到的是数据集`RowSet`

## 根据条件查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过使用`find`命令，可以查询集合中符合要求的文档，但是我们对于数据库的使用不是只限定在这种简单的操作，还有一些比较通用的查询要求，比如：范围查询等。

### 条件查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在SQL查询中，可以使用`>`或者`<`来进行范围的控制，也就是比较操作符。在MongoDB中，由于查询的语句都是JSON，所以需要用转移字符来替换掉我们常用的比较操作符。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对应关系如下表：

|类型|描述|
|---|---|
|`$lt`|less than，也就是 <|
|`$lte`|less than or equal，也就是 <=|
|`$gt`|greater than，也就是 >|
|`$gte`|greater than or equal，也就是 >=|
|`$ne`|not equal, 也就是 <>|

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;根据`age`进行范围查询，先查询`age`小于等于`21`的。

```sh
> db.foo.find();
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
> db.foo.find({"age": {"$lte" : 20}})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
> db.foo.find({"age": {"$gt" : 20}})
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到查询语句中，先给出了限定的文档字段`age`，随后跟着对字段的限定内容。、

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ConditionFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$gt", 20);

query.put("age", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

### `$IN`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在关系数据库中，可以使用`in`查询来进行单key的多值查询，比如：

```sql
select * from user where age in (18, 19, 20);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述SQL可以查询`age`为18、19或者20的所有`user`，而这种查询方式，MongoDB也有提供，方式和SQL很类似。

```sh
> db.foo.find()
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
> db.foo.find({"age": {"$in": [20,21]}})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到查询语句中，对于文档属性`name`进行了限定，限定描述是`$in`，同时指定值是`[20, 21]`。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.InFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$in", new int[]{20, 21});

query.put("age", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`$in`操作符还是用来限定文档的一个属性，而通过`$or`可以连接对于多个属性的限定。

### `$OR`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在关系数据库中，可以通过`or`来限定查询条件，MongoDB也提供了类似的解决方案，也就是使用`$or`操作符。

```sh
> db.foo.find({"$or": [{"name": "test"}, {"age": 21}]})
{ "_id" : ObjectId("5d9ab1e940eabd2b62ced66f"), "name" : "test", "age" : 20 }
{ "_id" : ObjectId("5dda6f29f75cb1b4beb2d95f"), "name" : "x", "age" : 21 }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述查询语句会查询集合`foo`中，`name`属性为`test`，`age`属性为`21`的文档。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.OrFindTest`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject nameQuery = new BasicDBObject();
nameQuery.put("name", "test");

DBObject ageQuery = new BasicDBObject();
ageQuery.put("age", 21);

query.put("$or", new DBObject[] {nameQuery, ageQuery});

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`$or`操作符可以连接多个对于不同（或者相同）属性的查询限定，在一定程度上要比`$in`灵活。同理也可以用`$and`操作符来连接多个查询条件。

## 高级查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;前文介绍了基本的查询功能，能够指定集合中文档的键进行精确或者范围查询，上述基本的查询功能也覆盖了大部分关系数据库中的查询方法。这些基本的查询方法对应的是基础数据类型，由于MongoDB提供了数组和子文档，这就使得查询这些特有的数据类型需要更为强大的查询方法，而这些也是MongoDB优于传统关系数据库查询功能的关键。

### 数组查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数组类型是一组相同类型的集合，比如：

```json
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中`value`属性存储的内容就是数组，数组元素的类型是INT。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果查询时，查询条件是数组中的一个元素，则一旦文档中的数组元素包含了这个元素，就会返回。

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

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到，`db.foo.find({"value": 3})`可以查询出所有`value`数组属性包含了`3`的文档。

#### 精确匹配`$all`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;按照`db.foo.find({"value": 3})`是一种单值的匹配方式，有时我们需要一种使用数组的匹配方式，比如：找出`value`包含了`[1, 2]`两个元素的文档，这就需要使用`$all`操作符了。

```sh
> db.foo.find({"value": {"$all": [1,4]}})
> db.foo.find({"value": {"$all": [1,2]}})
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到当输入的数组元素是`[1, 2]`时，就可以查询到对应的文档。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ArrayFindTest#all`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$all", new int[]{1, 2});

query.put("value", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到针对`value`这个属性，使用的查询限定是`{"$all": [1, 2]}`，表示查询`value`中全部包含了`1`和`2`元素的文档。

#### 长度匹配`$size`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;我们需要查询数组长度为3的文档，可以使用如下方式：

```sh
> db.foo.find({"value": {"$size": 3}})
{ "_id" : ObjectId("5de3abd5c28644d98389fadd"), "name" : "haha", "value" : [ 1, 2, 3 ] }
{ "_id" : ObjectId("5de3abffc28644d98389fade"), "name" : "hehe", "value" : [ 3, 4, 5 ] }
> db.foo.find({"value": {"$size": 2}})
> 
```

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.ArrayFindTest#all`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$size", 3);

query.put("value", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到针对`value`这个属性，使用的查询限定是`{"$size": 3}`，表示查询`value`数组的长度为3的文档。

### 子文档查询

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;子文档是在文档的一个属性中存储另一个文档，好比自定义数据结构中包括了另外的自定义数据结构。子文档的模式如下所示，其中`map`属性存储的内容就是一个文档，也就是当前文档的子文档。

```json
{ 
    "_id" : ObjectId("5deccfaf65cd643b1a7f1b0f"), 
    "name" : "m", "age" : 20, 
    "map" : 
    { 
        "math" : 90, 
        "physics" : 91, 
        "english" : 92 
    } 
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;子文档的查询分为两种方式，一种是当做属性来看，也就是全匹配，另外一种是根据子文档的部分属性进行匹配查询。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;先看一下全匹配，也就是在查询的时候，限定的内容是对子文档的完整描述。

```sh
> db.foo.find({"map":  {"math": 90, "physics": 91, "english": 92}})
{ "_id" : ObjectId("5deccfaf65cd643b1a7f1b0f"), "name" : "m", "age" : 20, "map" : { "math" : 90, "physics" : 91, "english" : 92 } }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这种查询方式没有什么意义，因为给出子文档的全部内容，成本太高了，所以根据子文档的部分属性进行查询是一种常用的方式。而描述子文档的部分内容（属性）就需要利用`.`操作符，比如：要查询`math`为90的文档，可以这样描述`map.math=90`。

```sh
> db.foo.find({"map.math": 90})
{ "_id" : ObjectId("5deccfaf65cd643b1a7f1b0f"), "name" : "m", "age" : 20, "map" : { "math" : 90, "physics" : 91, "english" : 92 } }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;还可以这样查询，查询`math`属性高于80的全部文档。

```sh
> db.foo.find({"map.math": {"$gte": 80}})
{ "_id" : ObjectId("5deccfaf65cd643b1a7f1b0f"), "name" : "m", "age" : 20, "map" : { "math" : 90, "physics" : 91, "english" : 92 } }
{ "_id" : ObjectId("5decd04865cd643b1a7f1b10"), "name" : "n", "age" : 19, "map" : { "math" : 80, "physics" : 81, "english" : 82 } }
```

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.InnerFindTest#inner_doc_property`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$gte", 80);

query.put("map.math", condition);

DBCursor dbObjects = collection.find(query);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到针对`map.math`这个属性，也就是子文档`map`中的`math`属性，使用的查询限定是`{"$gte": 80}`，表示查询`map.math`中大于等于80的文档。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在看到数组和子文档后，如果数据结构再复杂一些，也就是数组中的元素不再是基本类型，而是文档类型，那么如何进行查询呢？类似如下文档：

```json
{ 
    "_id" : ObjectId("5dece6af65cd643b1a7f1b11"), 
    "name" : "x", 
    "age" : 27, 
    "maps" : 
    [ 
        { 
            "math" : 90, 
            "physics" : 91, 
            "english" : 92 
        }, 
        { 
            "math" : 80, 
            "physics" : 81, 
            "english" : 82 
        } 
    ] 
}
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;对于这种复杂的文档进行查询时，首先可以看到`maps`是一个数组，对于数组查询可以使用完全给出数组中的元素或者一些特定的操作符，比如：

```sh
> db.foo.find({"maps": {"$size": 2}})
{ "_id" : ObjectId("5dece6af65cd643b1a7f1b11"), "name" : "x", "age" : 27, "maps" : [ { "math" : 90, "physics" : 91, "english" : 92 }, { "math" : 80, "physics" : 81, "english" : 82 } ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以通过`$size`操作符，来查询`maps`属性（数组）包含了2个元素的文档，又或者给出数组元素中的完整内容进行查询。

```sh
> db.foo.find({"maps": {"$all": [{ "math" : 90, "physics" : 91, "english" : 92 }]}})
{ "_id" : ObjectId("5dece6af65cd643b1a7f1b11"), "name" : "x", "age" : 27, "maps" : [ { "math" : 90, "physics" : 91, "english" : 92 }, { "math" : 80, "physics" : 81, "english" : 82 } ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果是完全给出匹配内容的查询方式，意义不大，我们需要的是给出部分属性的查询，如果要查询`english`大于`80`的数据，按照如下方式：

```sh
> db.foo.find({"maps": {"$all": [{ "english" : {"$gt": 80} }]}})
> 
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以发现，并不会生效，原因就是在数组查询中，需要给出数组元素的全部内容。这种查询也无法使用`maps.english`来进行匹配，因为用这个key无法确定性的指定到子文档中的属性，因此需要使用`$elemMatch`来进行部分指定匹配数组中单个子文档的限定条件，仅当数组中是有多个键的子文档时，可以使用。

```sh
> db.foo.find({"maps": {"$elemMatch": {"math": {"$gte": 90}}}})
{ "_id" : ObjectId("5dece6af65cd643b1a7f1b11"), "name" : "x", "age" : 27, "maps" : [ { "math" : 90, "physics" : 91, "english" : 92 }, { "math" : 80, "physics" : 81, "english" : 82 } ] }
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;上述查询指定了`maps`数组属性中，所有的子文档中`math`属性大于等于90。

> 对于java端的操作可以参考：`com.murdock.books.mongodbguide.chapter4.InnerFindTest#inner_doc_elem_match`

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用MongoDB的Java客户端，进行查询文档的关键逻辑如下：

```java
DBObject query = new BasicDBObject();

DBObject condition = new BasicDBObject();
condition.put("$gte", 90);

query.put("math", condition);

DBObject elementMath = new BasicDBObject();

elementMath.put("$elemMatch", query);

DBObject arrayQuery = new BasicDBObject();

arrayQuery.put("maps", elementMath);

DBCursor dbObjects = collection.find(arrayQuery);
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以看到，查询的构筑过程已经非常复杂，首先是对于子文档的限定`{"math": {"$gte", 90}}`，随后将限定放置在`$elemMatch`的限定中，最后用`elemMatch`限定去匹配`maps`这子文档数组属性。

> 查询过程应该从最里层开始思考，逐步外推到父文档的属性。

## 游标