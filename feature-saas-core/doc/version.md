# 2.3.2.2-SNAPSHOT
> 新增功能：

> 更新功能:
> 
> 1.更新`操作记录diff对比`组件
>   支持对date字段进行格式化处理
>   默认使用YYYY-MM-DD格式化 支持使用 `@DateTimeFormat`注解自定义

# 2.3.2.1-SNAPSHOT

> 新增功能：

- 1.第三方调用鉴权功能`kfang.agent.features.saas.thirdparty`，使用方式参照：`service-infra-invoice`
  
- 2.请求次数限制组件`request.limit`新增组合注解
    `@AgentRequestLimits` 支持一个方法多种不同的限制方式
    使用试例
    ```java
    // 一天只能重发5次不同的发票
    // 一周只能重发35次不同的发票
    @AgentRequestLimits({
            @AgentRequestLimit(name = "reSendInvoiceToDay", limit = 5),
            @AgentRequestLimit(name = "reSendInvoiceToWeek", limit = 35, time = "7天")
    })
    public String reSendInvoice(IdExtendForm form) {}
 
    ```
> 更新功能:

- 1.更新`操作记录diff对比`组件
  增加注解`OperationLogField` 属性`isJoinModuleNameEveryone`
  支持对objList对象list每一个元素是否都要拼接模块名进行自定义
  
# 2.3.2-SNAPSHOT

> 第一版组件
