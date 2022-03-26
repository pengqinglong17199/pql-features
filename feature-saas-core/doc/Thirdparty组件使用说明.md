# 使用场景

> 用于调用第三方系统接口鉴权
>
> 比如发票服务调用第三方易票云服务

# 使用方法

## 引入依赖

```xml

<dependency>
  <groupId>com.kfang.agent</groupId>
  <artifactId>feature-saas</artifactId>
  <version>2.3.2-SNAPSHOT</version>
</dependency>
```

## 使用步骤

> 定义第三方配置类

```java
/**
 * 继承{@link ThirdpartyConfig}
 */
@Data
@Configuration
@RefreshScope
public class YiPiaoYunConfig implements ThirdpartyConfig {

  @Value("${yipiaoyun.url}")
  private String url;

  @Value("${yipiaoyun.client-id}")
  private String clientId;
}
```

> 定义第三方鉴权处理逻辑

```java
/**
 * 易票云鉴权处理器
 */
@Component
public class YiPiaoYunAuthentication implements ThirdpartyAuthentication<YiPiaoYunConfig, YiPiaoYunBaseForm> {

  public static final String YI_PIAO_YUN = "YIPIAOYUN";
  public static final String TOKEN_PATH = "/apiv2/oauth/token";
  public static final String SPACE = " ";

  @Resource
  private YiPiaoYunConfig yiPiaoYunConfig;

  @Resource(name = "agentCache")
  private KfangCache agentCache;

  @Override
  public YiPiaoYunConfig getConfig() {
    return yiPiaoYunConfig;
  }

  @Override
  public YiPiaoYunBaseForm initForm(YiPiaoYunBaseForm yiPiaoYunBaseForm) {

    // 获取token
    YiPiaoYunTokenVo token = this.getToken();

    // 封装鉴权请求头
    yiPiaoYunBaseForm.setAuthorization(token.getTokenType() + SPACE + token.getAccessToken());
    yiPiaoYunBaseForm.setMachineNo(yiPiaoYunConfig.getMachineNo());
    yiPiaoYunBaseForm.setTaxNo(yiPiaoYunConfig.getTaxNo());

    return yiPiaoYunBaseForm;
  }

  /**
   * 获取token
   */
  private YiPiaoYunTokenVo getToken() {

    // 从redis中获取token
    YiPiaoYunTokenVo tokenVo = (YiPiaoYunTokenVo) agentCache.get(InvoiceCacheType.YI_PIAO_YUN_ACCESS_TOKEN, YI_PIAO_YUN);
    if (tokenVo != null) {
      return tokenVo;
    }

    // redis中不存在token 请求接口再次获取token
    YiPiaoYunTokenVo nowToken = this.requestToken();

    // 将token再次存回redis
    String wrapKey = agentCache.wrapKey(InvoiceCacheType.YI_PIAO_YUN_ACCESS_TOKEN, YI_PIAO_YUN);
    agentCache.<RedisAction>doCustomAction(CacheOpeType.SAVE, redisTemplate -> {
      redisTemplate.opsForValue().set(wrapKey, nowToken);
      return null;
    });

    return nowToken;
  }

  /**
   * 请求token
   */
  private YiPiaoYunTokenVo requestToken() {

    // 封装入参
    Map<String, Object> param = MapUtil.newHashMap();
    param.put("client_id", yiPiaoYunConfig.getClientId());
    param.put("client_secret", yiPiaoYunConfig.getClientSecret());
    param.put("grant_type", yiPiaoYunConfig.getGrantType());
    param.put("username", yiPiaoYunConfig.getUsername());
    param.put("password", yiPiaoYunConfig.getPassword());

    // 请求token
    HyugaHttp.HttpResult post = HyugaHttpSingleton
            .init()
            .requestHeader(MapUtil.newHashMap("Content-Type", "application/x-www-form-urlencoded"))
            .post(yiPiaoYunConfig.getUrl() + TOKEN_PATH, param);

    // 序列化
    return JsonUtil.toJavaObject(post.getContent(), YiPiaoYunTokenVo.class);
  }

}
```

> 定义基础form

```java

/**
 * 易票云基础form，继承{@link ThirdpartyForm}
 */
@Data
public class YiPiaoYunBaseForm implements ThirdpartyForm {
  /**
   * 第三方请求实体注解
   * 该注解直接打在{@link ThirdpartyForm}的{@link Field}实现上
   */
  @AuthParam(value = "Authorization")
  private String authorization;

  @AuthParam
  private String taxNo;

  @AuthParam
  private String machineNo;
}
```

> 定义具体请求接口form

```java

/**
 * 易票云 发票库存form
 */
@EqualsAndHashCode(callSuper = true)
// 第三方请求实体注解,该注解直接打在{@link ThirdpartyForm}的子类实现上
@RequestEntity(path = "/apiv2/api/invoice/invoiceinfo/findSurplusInvoice")
@Data
public class YiPiaoYunInvoiceSurplusForm extends YiPiaoYunBaseForm implements InvoiceSurplusForm {

  @ApiModelProperty(value = "发票总类")
  private Integer kind;

  @Override
  public InvoiceThirdpartyEnum getInvoiceThirdparty() {
    return InvoiceThirdpartyEnum.SHENZHEN_HANGXIN_YIPIAOYUN;
  }

}
```

> 第三方接口调用+鉴权

```java
/**
 * form：具体第三方接口请求form，如上述 YiPiaoYunInvoiceSurplusForm
 * Integer.class：请求调用后第三方返回数据类型
 */
Integer result=RequestCoreHandle.request(form,Integer.class);
```

> 定义序列化处理逻辑

```java

@Component
public class YiPiaoYunParamSerialize implements ThirdpartySerialize<YiPiaoYunInvoiceSurplusForm, String> {

  @Override
  public String serialize(YiPiaoYunInvoiceSurplusForm form) {

    return String.format("kind=%s", form.getKind());
  }
}
```

> 定义请求form解析逻辑

```java

@Component
@Slf4j
public class YiPiaoYunResultHandle implements ResultHandle<YiPiaoYunBaseForm> {

  public static final String OPERATE_CODE = "operateCode";
  public static final String SUCCESS = "S";
  public static final String DATAS = "datas";

  @Override
  public <K> K handle(String result, Class<K> resultClass) {

    JSONObject jsonObject = JsonUtil.parseObject(result);
    String operateCode = jsonObject.getString(OPERATE_CODE);

    // 先判断接口是否请求成功
    if (Objects.equals(SUCCESS, operateCode)) {
      if (resultClass == Integer.class) {
        return ObjectUtil.cast(jsonObject.getInteger(DATAS));
      } else {
        return JsonUtil.toJavaObject(jsonObject.getString(DATAS), resultClass);
      }
    }

    log.error("易票云接口调用失败 返回结果:{}", result);

    throw new InvoiceModuleException(InvoiceReturnCode.G9901);
  }

}
```

## 核心代码

- RequestCoreHandle 请求核心处理器(含多块处理逻辑)
  - 通过请求form，读取第三方请求鉴权器接口 ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm>
  - 将ThirdpartyConfig初始化到请求form中
  - 读取请求form中@RequestEntity注解的url，以及请求解析模式等
  - 可自定义调用第三方时请求form序列化方式，如：`YiPiaoYunParamSerialize`，未实现使用默认序列化`DefaultJsonSerialize`
  - 使用请求代理器 请求 `String result = RequestDelegateExecuter.execute(url, form);`
  - 请求返回，通过结果解析器对结果进行解析(可以自定义解析器，如：YiPiaoYunResultHandle 活用默认解析器)
- ThirdpartyAuthenticationFactory 第三方请求鉴权器工厂类
  - 类加载时，通过上下文读取所有ThirdpartyAuthentication类型的自定义第三方鉴权类，注入Map中`Map<Class<? extends ThirdpartyForm>, ThirdpartyAuthentication<ThirdpartyConfig, ThirdpartyForm>>`
  - 并提供方法`getAuthentication(ThirdpartyForm form)`根据请求form获取对应的鉴权器进行处理

## 注意事项





