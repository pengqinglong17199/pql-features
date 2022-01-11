# 使用场景

> 用于部分敏感数据业务接口需要隐藏返回值的敏感数据
> 
> 比如房源业主接口的返回 需要隐藏业主号码的前3后4

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

`@AgentHidden`

## 核心代码

- HiddenResultAspect        逻辑切面
- HiddenResultSensitiveInfo 方法注解
- HiddenFieldSensitiveInfo  字段注解

## 使用步骤
    1. 需要隐藏返回值的方法上打上注解 @HiddenResultSensitiveInfo
    2. 在返回类的对应字段上打上@HiddenFieldSensitiveInfo注解 通过注解属性进行定制化权限以及隐藏方式
        a. replaceStr   替换原数据的字符串 默认为空 优先级最高 如果有这个 下面全部作废
        b. head         头部保留位数
        c. tail         尾部保留位数
        d. mask         去头去尾后中间替换的掩码字符
        e. maskSize     数据掩码数量 此字段大于0时 tail失效

## 注意事项
    
    1.如果A对象嵌套B对象为字段，B对象的phone需要隐藏 在phone字段上增加@HiddenField注解后，
      还需要在A对象的B字段上增加@HiddenField注解 才会生效
       
      这里是考虑了对象权限细化的情况 可以满足以下情况
      比如说B对象 在作为A对象的成员变量时需要隐藏，
      在作为C对象的成员变量返回时不需要隐藏,就可以细分出来
