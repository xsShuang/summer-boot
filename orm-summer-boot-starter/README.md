# 基于mybatis-plus的多表关联查询解决方案

编写人 | 时间 | 说明
---|--- |---
谢霜 | 2020/11/29 | 初版完成

## 名词定义

VO：返回对象

Query：查询对象

DTO：数据转换对象

## 思路

1.解析VO，根据VO生成查询sql

2.解析Query，根据Query生成条件语句

3.调用mybaits进行查询，返回结果为json，直接将json转为java对象

4.解析VO里的关联对象，一条语句查出所有关联对象，避免N+1问题。

## 注解说明

1.Select

包含两个属性value和exist，value属性表示查询字段的sql语句，默认空。exist表示是否查询当前字段
