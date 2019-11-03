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

&nbsp;&nbsp;&nbsp;&nbsp;

## 根据条件查询

## 高级查询

## 游标