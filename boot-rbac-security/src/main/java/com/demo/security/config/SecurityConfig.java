package com.demo.security.config;

import com.demo.security.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    /**
     * 用于将登录状态保存到数据库中，在浏览器关闭之后，仍然可以访问对应的链接
     * <p>
     * 这里需要默认的在数据库中创建一张表  persistent_logins
     * remember-me 功能实现
     *
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    // 注入 PasswordEncoder 类到 spring 容器中
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 设置没有权限访问跳转自定义页面
        http.exceptionHandling().accessDeniedPage("/unauth.html");

        // 退出,这里的/logout的请求是和前端的接口约定，是security给我们提供的,退出成功后跳转登录页/login.html
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login.html").permitAll();

        // 设置记住我
        http.rememberMe()
                .tokenRepository(persistentTokenRepository())
                // 设置有效时长180秒，默认 2 周时间。
                .tokenValiditySeconds(180)
                .userDetailsService(myUserDetailsService);

        //  表单登录
        http.formLogin()
                // 修改默认的登录页为login.html，他会自动去根路径static文件夹下寻找login.html
                .loginPage("/login.html")
                // 设置登录接口地址，这个接口不是真实存在的，还是用的security给我们提供的，之所以要有这个配置，是login.html当中form表单提交的地址我们设置的是这个
                .loginProcessingUrl("/user/login")
                // 登录成功之后跳转的页面
                .defaultSuccessUrl("/home.html")
                // 登录失败之后跳转的 url
                .failureForwardUrl("/fail")
                // permitAll中文意思是许可所有的：所有的都遵循上面的配置的意思
                .permitAll();

        //  身份认证
        http.authorizeRequests()
                // 该路由不需要身份认证
                .antMatchers("/user/login", "/login.html").permitAll()
                // 需要用户带有管理员权限
                .antMatchers("/findAll").hasRole("管理员")
                .antMatchers("/find").hasRole("管理员")
                // 需要用户具备这个接口的权限
                .antMatchers("/find").hasAuthority("menu:user")
                // 任何请求都需要认证
                .anyRequest().authenticated();
        // 关闭 csrf
        http.csrf().disable();
    }
}
