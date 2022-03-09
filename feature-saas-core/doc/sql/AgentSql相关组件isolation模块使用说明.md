# 使用场景

> 
> 用于dao层方法 检测与拼接 一些需要数据隔离的相关sql语句
> 
> 比如说目前的 盘客saas化 以平台id对所有查询数据进行隔离 也就是所有sql都需要加上平台id的条件

# 使用方法

## 引入依赖

```xml

<dependency>
    <groupId>com.kfang.agent</groupId>
    <artifactId>feature-saas</artifactId>
    <version>2.3.2-SNAPSHOT</version>
</dependency>
```

## 启动类开启注解

`@AgentSql(isolation=true)` 
 - isolation默认为false 不启用 修改为true启用

## 用法讲解
    1. dao层类上打上注解 @DataIsolationDao 即可对该类的所有方法进行隔离检测
    2. 部分全量扫表的 比如刷es等初始化方法 需要跳过隔离的 
       在方法上打上注解 @SkipDataIsolation 即可跳过隔离
    3. 系统来源为 DTS/JMS服务的会自动跳过隔离检测

## 核心代码

- DataIsolationInterceptor
- @DataIsolationDao
- @SkipDataIsolation
