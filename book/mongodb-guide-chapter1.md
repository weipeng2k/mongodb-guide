# 简介

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB是一种文档类型的数据库，具备了扩展性强的特性，支持诸如：辅助索引、范围查询以及排序等功能。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在主要功能基础上，还支持一些高阶特性，比如：内置的MapReduce支持和地理空间索引的支持。

## 丰富的数据模型

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB是面向文档的数据库，放弃关系模型的主要原因就是为了获得更加方便的扩展性。在关系数据库中的 **行** 在MongoDB中是灵活的 **文档**，一行记录就是一个文档，而文档是没有模式的，文档中的键不会实现定义也不会固定不变。

## 容易扩展

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**T**级别的数据在IoT大发展时代变得很寻常了，开发者需要从一开始就要考虑数据量级，而MongoDB在最初的设计中就考虑了扩展的问题，能够在多个服务器上分割数据，也就是从一开始就考虑支持数据的分片。

## 丰富的功能

|功能|描述|
|-----|-----|
|索引|MongoDB支持通用辅助索引，能够快速的增加一些索引|
|Javascript运作|MongoDB能够在服务端进行Javascript运行|
|聚合|MongoDB支持MapReduce以及多种聚合工具进行查询|
|固定集合|限制集合上限（表的行上限）|
|文件存储|MongoDB底层的GridFS可以用来完成一些大文件的分布式存储|

## 性能

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;性能是MongoDB主打的目标，默认的存储引擎使用了内存映射文件，而动态查询优化器会优化查询过程，同时MongoDB将服务端的一些处理逻辑放在客户端，使得MongoDB具备了非常好的性能。

## 容易运维

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDB尽可能的简化运维工作，只需要建立起主从的拓扑结构，当分布式环境中出现了故障，系统会自动完成主备切换，当新增节点加入是，会自动集成和配置新加入的节点。