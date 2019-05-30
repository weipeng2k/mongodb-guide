# 入门

&nbsp;&nbsp;&nbsp;&nbsp;文档是MongoDB的基本单元，类似于关系数据库中的行，而集合是文档的聚合，因此集合类似关系数据库中的表，在一个集合中可以装在不同模式的文档。每个文档都会有一个`_id`的主键。

&nbsp;&nbsp;&nbsp;&nbsp;一个MongoDB实例可以建立多个数据库，每个数据库都可以创建多个集合，而MongoDB通过基于`JavaScript`的shell来管理数据库。

## 文档

&nbsp;&nbsp;&nbsp;&nbsp;文档是MongoDB的核心概念，在MongoDB中一个文档就是一个键值对的集合，例如：`{"foo", 3, "bar":"4"}`。文档的键是字符串，且不能有重复的键出现。

## 集合

&nbsp;&nbsp;&nbsp;&nbsp;一组文档会形成集合，一般来说集合是无模式的，你可以把不同类型的文档都放在一个集合中，但是这样对于开发者来说，就是一个噩梦。将相同类型的文档放置在一个集合中，会使数据更加集中，同时也更利于查询，因为索引是根据集合来建立的，这样相同的文档在一个集合中，索引将会更有效。

&nbsp;&nbsp;&nbsp;&nbsp;数据库的命名一般是小写，有一些数据库是默认保留的。

> 在安装了Docker的机器上，运行`sudo docker run --name mongo-test -d -p 27017:27017 mongo:3.6.9`，会拉起一个MongoDB（版本是3.6.9），同时端口是27017。

&nbsp;&nbsp;&nbsp;&nbsp;保留的数据库是： **admin**，**local**和**config**。