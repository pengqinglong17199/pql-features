# 使用场景

> 测试多环境隔离，除了54环境作为默认环境其他测试环境和开发环境服务之间隔离
>
> 本地环境个别服务不想启动的，可以使用默认环境服务或者修改agent-feign.properties配置

# 主要配置

kfang.agent.feature.saas.constants.FeignConstants

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

`@AgentFeign(isolation = true, serviceSign = ServiceSignEnum.AGENT)`

## 核心代码

- AgentFeignConfiguration
- AgentFeignCore
- FeignBuilderHelper

# agent-feign.properties

```
# 默认测试环境IP后缀
default-env-ip-suffix=54
# 房源
service-agent-house=0
# 房源ES
service-agent-house-es=0
# 房源预录入
service-agent-house-pre=0
# 客源
service-agent-customer=0
# 客源es
service-agent-customer-es=0
# oms
service-agent-oms=0
# 投诉举报
service-agent-complaint=0
# 摄影实勘
service-agent-photographer=0
# 第三方
service-agent-thirdparty=1
# report
service-agent-report=0
# open
service-agent-open=0
# jms
service-agent-jms=0
# dts
service-agent-dts=0

```