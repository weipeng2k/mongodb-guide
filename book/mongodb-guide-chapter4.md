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

## 高级查询

## 游标