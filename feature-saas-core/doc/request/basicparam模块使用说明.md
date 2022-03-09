# basicparam 基础form参数填充
## 使用场景
>
> 用于web服务接口对form的基本信息参数进行填充
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

`@AgentRequest(basicParam = true, operatorSystem= "当前系统的枚举名")`

- basicParam 默认开启
- operatorSystem 也就是OperatorSystem枚举的name

### 注意事项
> 目前只切面了GetMapping PostMapping RequestMapping 三个web注解 如有需要 自行补充
### 核心代码

- RequestBasicParamsInjectAspect
- RequestBasicParamsProcessor