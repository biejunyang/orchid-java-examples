1、pom依赖添加
2、安全规则配置
        http.authorizeRequests().anyRequest().authenticated()
3、认证方式、认证端点设置
         * 1、需要认证的请求设置
         * 2、认证端点设置
         *      GET     /login：自定义登录页面设置
         *      POST    /login：用户登录校验端点、使用spring security默认提供的认证端点时、需要禁用csrf功能
         *      GET     /login?myerror：登录失败端点、默认为/login?error
         * 3、登录成功处理
         *    AuthenticationSuccessHandler
         *     defaultSuccessUrl():默认登录成功后将重定向到之前访问的页面、否则将重定向到此设置的页面SavedRequestAwareAuthenticationSuccessHandler
         *
         * 4、登录失败处理
         * 3、其他功能如：注销、记住
    
4、获取用户信息配置
