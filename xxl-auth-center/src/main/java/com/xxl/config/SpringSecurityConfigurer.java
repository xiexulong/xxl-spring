package com.xxl.config;


import com.xxl.config.filter.LoginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SpringSecurityConfigurer这个配置类是Spring Security的关键配置。在这个配置类中，我们主要做了以下几个配置：
 * 1. 访问路径URL的授权策略，如登录、Swagger访问免登录认证等
 * 2. 指定了登录认证流程过滤器 JwtLoginFilter，由它来触发登录认证
 * 3. 指定了自定义身份认证组件 JwtAuthenticationProvider，并注入 UserDetailsService
 * 4. 指定了访问控制过滤器 JwtAuthenticationFilter，在授权时解析令牌和设置登录状态
 * 5. 指定了退出登录处理器，因为是前后端分离，防止内置的登录处理器在后台进行跳转
 */
@Configuration
@EnableWebSecurity  //通过@EnableWebSecurity注解开启Spring Security的功能
public class SpringSecurityConfigurer extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfigurer.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${login.path}")
    private String loginUrl;


    /**
     * Initialize configure resource.
     *
     * @param http http.
     * @throws Exception exception.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //// 禁用 csrf, 由于使用的是JWT，我们这里不需要csrf
        http.csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().anonymous().disable()
                .addFilterAfter(loginFilter(), UsernamePasswordAuthenticationFilter.class);


//        http.csrf().disable();


        // 扩展
//        http.authorizeRequests() //通过authorizeRequests()定义哪些URL需要被保护、哪些不需要被保护
//                .antMatchers("/", "/home").permitAll() // /home 和/ 不需要认证其他的都需要认证
//                .anyRequest().authenticated()
//                .and()
//            .formLogin() //定义当需要用户登录时候，转到的登录页面
//                .loginPage("/login")
//                .permitAll()
//                .and()
//            .logout()
//                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //// 自定义登录身份认证配置
        auth.jdbcAuthentication().passwordEncoder(passwordEncoder)
                .dataSource(dataSourceConfig.getDataSource());
        auth.eraseCredentials(false);
    }


    @Bean
    public LoginFilter loginFilter() throws Exception {
        logger.info("Init LoginFilter, loginUrl: {}", loginUrl);
        return new LoginFilter(loginUrl, authenticationManager());
    }
}
