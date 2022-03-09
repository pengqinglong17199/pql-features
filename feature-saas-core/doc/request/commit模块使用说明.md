# commit 防止重复提交组件
## 使用场景
>
> 用于部分业务接口防止 重复人员的重复参数的重复请求反复打到后台的情况下进行过滤
>
> 
## 启动注解
kfang.agent.feature.saas.request.AgentRequest


## 使用方法

### 引入依赖

```xml

<dependency>
    <groupId>com.kfang.agent</groupId>
    <artifactId>feature-saas</artifactId>
    <version>2.3.2-SNAPSHOT</version>
</dependency>
```

### 启动类开启注解

`@AgentRequest`

### 需要拦截重复请求的接口开启注解
`@RepeatedRequests(time=1000L)`
- time 重复请求间隔时间 单位 毫秒

### 核心代码

- BlockRepeatedRequests