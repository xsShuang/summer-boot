 ## 什么是 summer-boot
summer-boot 是基于 SpringBoot 封装的模块化后端快速开发框架，集成了常用日志、权限、定时任务和文件管理等业务功能，同时还封装了大量便捷开发的工具类和方法，为快速开发提供支撑。使用此框架，可以快速开发出一个web应用的后端。

目前开放模块：orm-summer-boot-starter orm工具模块

### orm-summer-boot-starter orm工具模块
此模块是对mybatis-plus的封装，在其基础上增加了关联查询，注解条件查询等功能

##### 常用对象
- `PageDTO`：分页参数，分页接口参数对象一般都要继承它
- `TimeDTO`：时间查询参数对象，定义了常用的时间查询参数`beginTime`、`endTime`、`timeSort`，同时它继承了`PageDTO`
- `OrderByDTO`：排序DTO类，前端自定义排序时会用到
- `CommonQueryInterface`：通用查询参数定义接口
- `CommonQueryDTO`: 通用查询DTO，继承了`TimeDTO`，实现了`CommonQueryInterface`，功能非常强大，基本任何查询都可以使用这个类作为参数

##### 通用查询dao
- `IMapper`：一个通用的查询mapper，封装了大量的通用方法，CRUD都具备，查询出来的对象都是jsonobject
- `BaseDao`：通用的查询dao定义接口
- `AbstractBaseDaoImpl`：通用的查询dao的抽象实现，内部是注入了`IMapper` 来调用基础的通用方法
- `DefaultDaoImpl`：通用的查询dao默认实现，其继承至`AbstractBaseDaoImpl`，可以通过 `@Autowired` 注入使用

##### 注解条件查询
通过在查询类上面增加注解的方式，自动生成查询wrapper，省略大量样板式代码
```java
    // 使用示例
    public IPage<ApiPermission> entityPage(ApiPermissionQuery query) {
        return this.page(PageDTO.page(query), new WrapperFactory<ApiPermission>().create(query));
    }
    // 根据query自动生成查询wrapper
    new WrapperFactory<ApiPermission>().create(query)
        
    // 分页标准示例：
    // controller层：
    @GetMapping("/page")
    public IResult<IPage<ApiPermission>> getPermissionPage(ApiPermissionQuery query) {
        return IResult.ok(apiPermissionService.entityPage(query));
    }
    
    // service层：
    IPage<ApiPermission> entityPage(ApiPermissionQuery query);
    
    // serviceImpl层：
    @Override
    public IPage<ApiPermission> entityPage(ApiPermissionQuery query) {
        return this.page(PageDTO.page(query), new WrapperFactory<ApiPermission>().create(query));
    }

// 查询类说明
// 继承至 TimeDTO ，拥有根据时间查询的能力
public class ApiPermissionQuery extends TimeDTO implements Serializable {

    private static final long serialVersionUID=1L;

    // QueryCondition注解，默认是EQ查询
    @ApiModelProperty(value = "授权标识")
    @QueryCondition
    private String permissionCode;

    // QueryCondition注解，通过 condition = QueryCondition.Condition.LIKE 来标注是like查询
    @ApiModelProperty(value = "名称")
    @QueryCondition(condition = QueryCondition.Condition.LIKE)
    private String permissionName;

    // QueryCondition注解，通过 condition = QueryCondition.Condition.DEFAULT 来标注此字段不参与查询，
    // sort = QueryCondition.Sort.ASC 来标注根据此字段顺序进行排序
    @ApiModelProperty(value = "排序号")
    @QueryCondition(condition = QueryCondition.Condition.DEFAULT, sort = QueryCondition.Sort.ASC)
    private Integer sort;
```
- `@QueryCondition`：查询注解，同在字段上，用以标识查询相关信息
```java
    /**
    * 查询条件枚举类
    * DEFAULT，不进行查询
    */
    enum Condition{
        DEFAULT, EQ, IN, LIKE, GE, LE, SIN, LIN, IIN,GT,LT,NE
    }

    /**
    * Sort为排序字段，
    * DEFAULT,不进行排序
    * DESC，倒叙
    * ASC，顺序
    * AUTO，根据值进行排序，当值为数值型时，0代表顺序，其余都为倒叙
    * 当值为string类型时，asc和ASC为正序，其他都为逆序
    * 其余情况均为逆序
    */
    enum Sort{
        DEFAULT, DESC, ASC, AUTO
    }

    /**
    * 查询条件
    * @return
    */
    Condition condition() default Condition.EQ;

    /**
    * 数据库字段，默认为空，自动根据驼峰转下划线
    * @return
    */
    String field() default "";

    /**
    * 排序说明
    * @return
    */
    Sort sort() default Sort.DEFAULT;
```
- `WrapperFactory`：wrapper生成工具，传入query对象根据注解直接生成wrapper
```java

    // 常用以下两个方法
    /**
     * 根据 dto 生成 QueryWrapper
     * @param dto
     * @return
     */
    public QueryWrapper<T> create(Object dto){
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        create(wrapper, dto, null);
        return wrapper;
    }

    /**
     * 根据 dto 生成 QueryWrapper，并指定表名
     * @param dto
     * @param tableName
     * @return
     */
    public QueryWrapper<T> create(Object dto, String tableName){
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        create(wrapper, dto, tableName);
        return wrapper;
    }
```

##### 关联查询

- `@JoinExpression`：关联表达式注解，用在查询类上，用以表示跟连接表之前的连接关系
```java
    public @interface JoinExpression {

        /**
        * 表达式值
        */
        String value();

        /**
        * 是否一直参与join，主要针对场景是即使未传入查询值，也需要join查询，例如连表排序
        */
        boolean allJoin() default false;

    }

    // 使用示例
    // value = "join sys_web_dic_main m on m.id = sys_web_dic_info.main_id";
    // 会在查询sql后面拼接这个连表条件
    // 传入字段为dicCode，但是QueryCondition注解中field=m.dic_code，所以sql拼接的查询条件字段为m.dic_code
    @JoinExpression(value = "join sys_web_dic_main m on m.id = sys_web_dic_info.main_id")
    @ApiModelProperty(hidden = true, value = "后端内部使用，无需传值")
    @QueryCondition(condition = QueryCondition.Condition.EQ, field = "m.dic_code")
    private String dicCode;
```

- `@MDS`：多数据源注解，用在VO类上，表示查询此VO时使用的数据源是什么
```java
    public @interface MDS {

        /**
        * 数据源名称
        */
        String value();
    }
```

- `@Select`：VO查询字段注解，用在VO类的字段上，定义查询时此字段的名称以及是否需要查询
```java
    public @interface Select {

        /**
        * VO查询注解，定义查询的字段
        */
        String value();

        /**
        * 定义该字段是否需要进行查询，默认所有字段都会进行查询，不参与查询的需要添加此注解，并设置为false来排除
        */
        boolean exist() default true;

    }
```

- `@JoinSelect`：关联查询注解，用在VO类的字段上，定义子对象跟VO对象在数据库中的关联关系，通过什么样的关联sql去进行查询
```java
    public @interface JoinSelect {

        /**
        * 自动构建的sql语句末尾添加的sql
        */
        String and() default "";

        /**
        *关联信息表名
        */
        String relationName();

        /**
        * 中间表名
        */
        String middleTable() default "";

        /**
        * 表的主信息id，主信息对象的id字段，java对象字段
        */
        String mainId() default "id";

        /**
        * 表的附属信息id，数据库字段
        */
        String relationId() default "id";

        /**
        * 中间表的主信息id，数据库字段
        */
        String middleMainId() default "id";

        /**
        * 中间表的附属信息id，数据库字段
        */
        String middleRelationId() default "id";

        /**
        * 单个时的数据库字段
        */
        String field() default "";

        /**
        * 直接用sql查询
        */
        String sql() default "";

    }
```

- `AssociationQuery`：关联查询类，直接 **new** 然后调用查询方法进行VO查询
```java

    // 带参数的构造方法，传入的eClass为需要查询的VO类的class对象
    public AssociationQuery(Class<E> eClass){
        this.eClass = eClass;
    }

    // 常用的 vo 分页数据查询方法，传入的对象为继承了 PageDTO 的查询对象
    // 有5个重载方法，可以满足不同场景需求
    public IPage<E> voPage(PageDTO pageQuery){
        return voPage(pageQuery, new WrapperFactory<>().create(pageQuery, eClass));
    }

    // 常用的 vo 集合数据查询方法，传入的对象为查询对象
    // 有8个重载方法，可以满足不同场景需求
    public List<E> voList(Object query){
        return voList(new WrapperFactory<>().create(query, eClass), QueryUtil.generateSql(eClass, query), true);
    }

    // 常用的获取单个 vo 数据查询方法，传入的对象为id
    // 有13个重载方法，可以满足不同场景需求
    public E getVo(Object id){
        return getVo(id, "id");
    }

    // 常用的 count 数据查询方法，传入的对象为查询对象
    // 有4个重载方法，可以满足不同场景需求
    public long count(Object query){
        return count(new WrapperFactory<>().create(query, eClass), QueryUtil.generateSql(eClass, query));
    }

    // 使用示例
    // 获取系统接口日志VO分页数据
    @ApiOperation(value="获取系统接口日志VO分页数据")
    @PostMapping("/voPage")
    public IResult<IPage<ApiLogVO>> getApiLogVoPage(@RequestBody ApiLogQuery query) {
        AssociationQuery<ApiLogVO> associationQuery = new AssociationQuery<>(ApiLogVO.class);
        return IResult.ok(associationQuery.voPage(query));
    }

    // 获取系统接口日志VO数据
    @ApiOperation(value="获取系统接口日志VO数据")
    @PostMapping("/getVo")
    public IResult<ApiLogVO> getApiLogVoById(@RequestBody @JSONID JSONObject jsonObject) {
        AssociationQuery<ApiLogVO> associationQuery = new AssociationQuery<>(ApiLogVO.class);
        return IResult.ok(associationQuery.getVo(jsonObject.getLong("id")));
    }
```

示例：[summer-boot-demo](https://github.com/xsShuang/summer-boot-demo)
