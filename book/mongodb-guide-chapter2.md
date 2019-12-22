# 入门

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;文档是MongoDB的基本单元，类似于关系数据库中的行，而集合是文档的聚合，因此集合类似关系数据库中的表，在一个集合中可以装在不同模式的文档。每个文档都会有一个`_id`的主键。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一个MongoDB实例可以建立多个数据库，每个数据库都可以创建多个集合，而MongoDB通过基于`JavaScript`的shell来管理数据库。

## 文档

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;文档是MongoDB的核心概念，在MongoDB中一个文档就是一个键值对的集合，例如：`{"foo", 3, "bar":"4"}`。文档的键是字符串，且不能有重复的键出现。

## 集合

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一组文档会形成集合，一般来说集合是无模式的，你可以把不同类型的文档都放在一个集合中，但是这样对于开发者来说，就是一个噩梦。将相同类型的文档放置在一个集合中，会使数据更加集中，同时也更利于查询，因为索引是根据集合来建立的，这样相同的文档在一个集合中，索引将会更有效。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数据库的命名一般是小写，有一些数据库是默认保留的。

> 在安装了Docker的机器上，运行`sudo docker run --name mongo-test -d -p 27017:27017 mongo:3.6.9`，会拉起一个MongoDB（版本是3.6.9），同时端口是27017。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;保留的数据库是： **admin**，**local**和**config**。

## 运行MongoDB Shell

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB自带了一个Javascript Shell，可以使用命令行的方式和MongoDB进行交互，可以使用它进行管理操作，检查状态等。

> 可以使用`sudo docker exec -it mongo-test mongo`，其中`mongo-test`是MongoDB对应的docker实例名称。

```shell
weipeng2k@weipeng2k-workstation:~$ sudo docker exec -it mongo-test mongo
MongoDB shell version v3.6.9
connecting to: mongodb://127.0.0.1:27017
Implicit session: session { "id" : UUID("af9c08c0-3bec-4677-b0df-86bb37b0a7c7") }
MongoDB server version: 3.6.9
> 
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以在mongodb shell中执行javascript程序，例如：

```shell
> x=200
200
> print(x)
200
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以使用`use $db`命令来进行数据库切换，比如：切换到`test`数据库。

```shell
> use test
switched to db test
> db
test
```

> 可以使用`show dbs`或者在一个数据库中使用`show collections`来查看当前MongoDB实例部署的数据库，以及当前数据库中的集合。这些命令和mysql很相似。

### 创建数据

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;可以在MongoDB Shell中进行数据的定义，然后通过执行`db.$collection_name.insert($variable)`来进行数据的插入，通过执行这个命令，以将一个文档插入到集合中。

```shell
> author = {"_class" : "com.murdock.books.mongodbguide.domain.Author", "name" : "Author-1111111", "age" : 21}
{
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-1111111",
	"age" : 21
}
> db.author_test_collection.insert(author)
WriteResult({ "nInserted" : 1 })
> db.author_test_collection.find({"name":"Author-1111111"})
{ "_id" : ObjectId("5cfa7edbb263c5548e111e25"), "_class" : "com.murdock.books.mongodbguide.domain.Author", "name" : "Author-1111111", "age" : 21 }
> 
```

### 获取数据

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建数据后，可以使用`db.$collection_name.find($query_expression)`来获取数据。除了使用对应的表达式进行查询（在后续的内容中进行详细介绍），还可以使用比较简单的方式进行获取，比如：使用`db.$collection_name.findOne()`，该查询会返回一个集合中的文档。

```shell
> db
test
> db.author_test_collection.findOne()
{
	"_id" : ObjectId("5c6533707ed82520f5504bcb"),
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 26
}
```

> 可以使用`db`命令来查询当前会话是使用的哪个数据库。

### 更新数据

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过使用一个给定的查询方式和目标文档，可以将集合中的文档进行更新。

```shell
> db.author_test_collection.findOne()
{
	"_id" : ObjectId("5c6533707ed82520f5504bcb"),
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 26
}
> updateAuthor = {  "_class" : "com.murdock.books.mongodbguide.domain.Author", "name" : "Author-0", "age" : 27 }
{
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 27
}
> db.author_test_collection.update({"name":"Author-0"}, updateAuthor)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.author_test_collection.findOne()
{
	"_id" : ObjectId("5c6533707ed82520f5504bcb"),
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 27
}

```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过上面的命令可以看出，对于 *Author-0* 这个名称的文档，通过`update`操作，将其 **age** 属性进行了更新。当然，除了更新文档中已经存在的内容，也可以增加一些属性。

```shell
> updateAuthor = {  "_class" : "com.murdock.books.mongodbguide.domain.Author", "name" : "Author-0", "age" : 27, "sex":"male" }
{
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 27,
	"sex" : "male"
}
> db.author_test_collection.update({"name":"Author-0"}, updateAuthor)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.author_test_collection.findOne()
{
	"_id" : ObjectId("5c6533707ed82520f5504bcb"),
	"_class" : "com.murdock.books.mongodbguide.domain.Author",
	"name" : "Author-0",
	"age" : 27,
	"sex" : "male"
}

```

### 删除数据

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果想删除文档，自然就可以使用`db.$collection_name.remove($query_expression)`。对于符合查询条件的文档，将会从集合中删除。

```sh
> db.author_test_collection.remove({"sex":"male"})
WriteResult({ "nRemoved" : 1 })
> db.author_test_collection.find({"name":"Author-0"})
```

> 可以看到只要符合文档中有属性 **sex** 为 `male`的，都会被删除。

## 数据类型

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB的文档类似于JSON，虽然JSON的数据表现能力很强，但是还是无法满足一些特定要求，比如：时间类型。所以MongoDB针对这种场景做了一些扩展。

|类型|描述|
|-----|----|
|null|空值|
|布尔|true或者false|
|整数|分为32位和64位|
|浮点数|64位双精度浮点数|
|字符串|字符数组，类似："string"|
|对象ID|在一个集合中唯一定位一个文档，ObjectId()|
|日期|Date|
|数组|值的集合或者列表，类似：["A", "B", "C"]|
|内嵌文档|文档是一级KV，如果在V中包含了KV，那么就是内嵌文档|

> 以上是主要的数据类型。

### 时间类型

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期类型存储的是从标准纪元开始的时间，如果换算到中国时区，需要+8。

```sh
> v = {"x": new Date()}
{ "x" : ISODate("2019-06-08T12:29:21.063Z") }
```

### 内嵌文档

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;相当于在MongoDB中的一个文档被嵌入到了另一个文档中，这种组合形式就不会让文档拘泥于扁平形式。而MongoDB支持在内嵌文档上进行索引建立，虽然数据可能有重复，但是形式表达会更加自然。

### 对象ID

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在MongoDB的任何文档中，都会有一个属性`_id`，它所存储的是这个集合中的唯一标识。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;默认情况下MongoDB的客户端会自动生成一个对象ID，它通过使用当前机器名、进程等标识信息生成了一个12byte的对象ID。类似Mysql的id autoincreament一样，能够生成不同的数据主键，但是MongoDB把这个工作放在了客户端，这样就减轻了服务端的压力。