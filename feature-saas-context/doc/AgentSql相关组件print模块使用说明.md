# 使用场景

> 测试环境下打印sql日志使用 方便排查问题

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

`@AgentSql(print=true)` 
 - print默认为true 可以不用参数

## 核心代码

- SqlPrintInterceptor
