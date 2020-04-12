1、使用介绍：
    https://segmentfault.com/a/1190000018779378
    https://www.cnblogs.com/yshang/p/11263172.html
    https://www.bbsmax.com/A/kmzLD1qYzG/
    
    
2、Security安全配置中放开权限控制：
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/swagger-ui.html")
                .antMatchers("/v2/**")
                .antMatchers("/swagger-resources/**");
    }


3、全局请求操作添加：
    参数类型支持：header, cookie, body, query etc
    a、构建全局参数：
        ParameterBuilder aParameterBuilder = new ParameterBuilder();
                aParameterBuilder.parameterType("header") //参数类型支持header, cookie, body, query etc
                        .name(jwtProperties.getHeader()) //参数名
                        .defaultValue("") //默认值
                        .description("JWt TOKEN")//描述
                        .modelRef(new ModelRef("string"))//参数类型
                        .required(false);//是否必须
                List<Parameter> aParameters = new ArrayList<Parameter>();
                aParameters.add(aParameterBuilder.build());
                
    
    b、设置全热群参数
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                        .apis(RequestHandlerSelectors.basePackage("com.orchid"))
                        .paths(PathSelectors.any())
                        .build().globalOperationParameters(aParameters);
                        
3、Swagger API访问添加权限控制：
    