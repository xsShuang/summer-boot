编写人 | 时间 | 说明
---|--- |---
谢霜 | 2020/02/28 | 初版完成
谢霜 | 2020/03/04 | 集成websocket
谢霜 | 2020/04/10 | id改成uuid
谢霜 | 2020/05/26 | id改成雪花id
谢霜 | 2020/12/02 | 增强mybatis-plus功能
谢霜 | 2020/12/02 | 增加通用统计jar包
谢霜 | 2021/04/15 | 更新springboot版本到2.3.9，cache功能修改为2级缓存

# 最新版 2022.1.9

### 1.项目模块说明

![mark](http://qiniuyun.westcatr.boot.xyz/mpic/20200228/5RrBO1bbVV5T.png?imageslim)

#### a-common-boot-starter

> 基础通用依赖，定义了一些基础注解以及工具类

#### a-cache-boot-starter

> java2级缓存依赖，集成了redis以及caffeine

#### b-mybatis-plus-boot-starter

> mybatis-plus依赖，包含代码生成器，自定义代码生成模板，多表关联查询等功能

#### b-web-boot-starter

> web环境依赖，添加了全局异常处理，ip以及地址工具类，以及大量参数校验注解

#### c-web-mybatis-boot-starter

> web以及mybatis-plus依赖，在引入b-mybatis-plus-boot-starter和b-web-boot-starter的同时，添加了两个常用dto对象，分别是PageDTO以及TimeDTO，还有唯一键异常处理策略

#### d-authority-boot-starter

> 权限依赖，引入后直接集成了权限的一整套功能

#### d-log-boot-starter

> 操作日志依赖，引入后直接集成了操作日记记录功能

#### d-file-boot-starter

> 文件模块依赖，引入后直接集成了文件管理的功能，文件上传以及下载

#### d-count-boot-starter

> 通用数据统计依赖，引入后直接集成了通用数据统计功能

#### d-dic-boot-starter

> 数据字典依赖，引入后直接集成了数据字典功能

#### 模块补充说明：前缀 a,b,c,d...的含义是，字母顺序越靠前，越基础，可能b会引用a，c会引用b以及a，但c绝不会引用d，同时这样也是为了让项目的层次结构更清晰（idea中，项目模块默认按字母排序）

### 缓存依赖说明

目前支持两种缓存，redis以及caffeine

#### 启用j2cache功能引入注解

EnableJ2Cache

#### 启用j2cache 方法缓存（springboot的缓存注解扩展）功能引入注解

EnableMethodCache

#### 以及以下配置：

```
# redis相关配置
spring.redis.database=1
spring.redis.host=127.0.0.1
spring.redis.password=123456
spring.redis.port=6379
spring.redis.timeout=5000
spring.redis.jedis.pool.max-active=16
spring.redis.jedis.pool.max-idle=16
spring.redis.jedis.pool.max-wait=5000
spring.redis.jedis.pool.min-idle=1
```

---

### 全局异常处理说明

代码中尽量不要有任何try catch语句（特殊情况除外），错误都直接返回即可。

#### 配置说明：

```
# 是否启用http状态码，默认false不需要配置，所有操作都会返回200，状态码在结果的status字段表示。为true时，按照标准的http状态码进行返回
enableHttpStatus=false
# 异常处理策略配置类，当有特殊异常需要进行处理时进行配置
exception.strategy.aliasMap.DuplicateKeyException=duplicateKeyExceptionStrategyImpl
```

#### 新增异常处理策略

异常处理采用策略模式，根据异常选择相应的异常类进行处理。当需要新增一种异常处理策略时，需要以下步骤：

##### 1.实现接口ExceptionStrategy

##### 2.根据实现接口的类名，新增配置文件

`westcatr.boot.web.alias-map.DuplicateKeyException=xxxxxxxExceptionStrategyImpl`

### 权限功能说明

权限功能采用拦截器进行实现。

思路：根据url获取对应的权限信息，然后匹配用户的权限，有则放行，否则拦截。

##### 启用权限功能需要引入注解：

EnableIAuthority

权限相关配置：

```
# 不拦截的路径，以逗号隔开
westcatr.boot.permission.noCheckUrl=/doc.html,/actuator/**,/swagger-ui.html,/webjars/**,/swagger-resources/**,/v2/api-docs/**,/,/csrf,/error,/login
```

##### 权限信息优先级：数据库 > 注解

（这样才能实现动态修改权限拦截的功能，当临时需要放开某个url时，在数据库进行相应的修改就行了）

#### 权限没有涉及用户表，用户表根据实际情况自行建设。只需要配置查询用户的sql以及表名即可，默认语句如下

```
# 用户信息查询语句，默认：select id,username,password,full_name,enable from org_user_info where username = '{}'}
westcatr.boot.permission.selectUserSql
# 是否允许一个账号同时登陆，默认true
westcatr.boot.permission.manyLogin
# 是否启用验证码，默认false
westcatr.boot.permission.captcha
# 保存登录的时长，默认604800
westcatr.boot.permission.saveTime
# 不保存登录的时长，默认7200
westcatr.boot.permission.noSaveTime

# 密码加盐，前盐
westcatr.boot.permission.beginSalt
# 密码加盐，后盐
westcatr.boot.permission.endSalt
```

##### 用户信息必须包含以下字段：

字段名称 | 字段说明 | 字段类型
---|--- |---
id | 用户id | int
username | 用户名 | string
password | 用户密码 | string
full_name | 真实姓名 | string
enable | 账号是否启用 | int

### 启用websocket

引入注解EnableIWebSocket即可

当需要发消息时，注入类 IWebSocketHandler

### 1.全局返回体

```
{
	"data": "",      // 返回数据
	"message": "",   // 消息
	"status": 0,     // 状态码，按照标准http状态码来
	"success": true, // 成功标志
	"timestamp": 0   // 时间戳
}
```

`当数据为分页数据时，所有分页相关数据全在data中，格式如下`

```
{
	"data": {
		"current": 0,  // 当前页码
		"hitCount": true,
		"pages": 0,    // 总页数
		"records": [], // 数据集合
		"searchCount": true,
		"size": 0,      // 每页条数
		"total": 0      // 总数
	},
	"message": "",      // 消息
	"status": 0,        // 状态码，按照标准http来
	"success": true,    // 成功标志
	"timestamp": 0      // 时间戳
}
```

### 2.权限处理

访问登录接口时，登录成功后，后台接口会返回给前端token，后面访问接口需要将token带上，`方式有两种：1放在header中；2直接跟在url后面`。参数名称统一为`token`
，例子: `url?a=xxx&b=xx&token=token`

### 3.默认请求方式

#### 1.查询

所有查询请求都为`get`方式，查询请求包含 list，page，getByxx

#### 2.新增，修改

所有新增修改请求都为`post`方式，以json进行传输

#### 3.删除

所有删除请求都为`delete`方式，参数统一为id，当时批量删除时，参数格式为 `delete?id=xxx,xx,xx`, id以逗号隔开

