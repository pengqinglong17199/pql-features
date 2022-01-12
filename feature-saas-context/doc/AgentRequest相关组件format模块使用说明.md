# format 防止重复提交组件
## 使用场景
>
> 用于业务接口对请求参数的一些格式化处理 
> 
> 例如
> 
> 1.字符串去除前后空格
> 
> 2.字符串去除表情符号

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

### 需要拦截重复请求的form类的字段上补充格式化相关注解
例如

`@StringFormat(front=boolean, rear=boolean, emoji=boolean)`
- front 是否去除头部空格
- rear 是否去除尾部空格
- emoji 是否去除emoji表情符

### 核心代码

#### 基础核心代码
- FormatHandle
- RequestParamFormatAspect
- RequestParamFormatHandle

#### 格式化逻辑实现
- StringFormatHandle

### demo演示
```java
public class Demo{
    public static void main(String[] args) throws Exception {

        TestDemo demo = new TestDemo();
        demo.setHouseId("   houseId  👴  TestDemo   ");

        TestDemoObj test1 = new TestDemoObj();
        test1.setGardenId("  你要买什么新房呀   ");
        demo.setObj(test1);

        TestDemoList test2 = new TestDemoList();
        test2.setUnitId("     ");
        TestDemoList test3 = new TestDemoList();
        test3.setUnitId("  test 3   ");
        TestDemoList test4 = new TestDemoList();
        test4.setUnitId(null);
        List<TestDemoList> list = ListUtil.newArrayList();
        list.add(test2);
        list.add(test3);
        list.add(test4);
        demo.setList(list);

        RequestParamFormatAspect requestParamFormatAspect = new RequestParamFormatAspect();
        Class<?> aClass = demo.getClass();
        // 循环获取所有字段包括父类的字段
        List<Field> fieldList = requestParamFormatAspect.getFieldList(aClass);

        // 循环所有字段 找对应的策略处理自己
        requestParamFormatAspect.handleFieldList(demo, fieldList);

        System.out.println(demo);
    }

    @Data
    public static class TestDemo {

        @StringFormat(emoji = true)
        private String houseId;

        @StringFormat
        private TestDemoObj obj;

        @StringFormat
        private List<TestDemoList> list;

    }

    @Data
    public static class TestDemoObj {

        @StringFormat(rear = false)
        private String gardenId;
    }

    @Data
    public static class TestDemoList {

        @StringFormat
        private String unitId;
    }
}
```