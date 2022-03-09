# limit 限制用户请求次数组件
## 使用场景
>
> 用于部分业务接口限制
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

### 启动注解开启限制 requestLimit

`@AgentRequest(requestLimit=true)`

### 需要限制用户请求次数的接口开启注解
`@AgentRequestLimit`
### 注解参数解释
>name
> 
> 请求接口的name 有可能多个接口都是一个功能 所以可以共同使用一个name进行限制

> imit 
> 
>一定时间内可请求的次数


>removes  
> 
> 用于清除其他name的限制 比如说a接口限制满了之后 调用b接口会清除掉a接口的限制

>time    
> 
> 从第一次请求开始计时的限制时长 默认为当天
> - 支持自定义格式
> - ?y?M?d?h?m?s
> - ?年?月?天?时?分?秒

>code     
> 
>到达限制后的自定义返回错误码   默认为E9001

>message  
> 
> 到达限制后的自定义返回错误说明 默认为当前请求次数已达上限
> 
### 核心代码

- AgentRequestLimitCore