package org.arturjoshi.config;

import org.arturjoshi.authentication.StatelessAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by ajoshi on 20-May-16.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(Ordered.LOWEST_PRECEDENCE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private StatelessAuthenticationFilter statelessAuthenticationFilter;

    public void setStatelessAuthenticationFilter(StatelessAuthenticationFilter statelessAuthenticationFilter) {
        this.statelessAuthenticationFilter = statelessAuthenticationFilter;
    }

    @Bean(name="authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .eraseCredentials(true)
                .userDetailsService(userDetailsService);
                // This  RFC2898PasswordMatcher is not intended for password encoding,
                // only for validating passwords with already encoded by bizplatform(.net) server.
                // (Bizplatform server performs user registration but they should be authenticated
                // on this server too )
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String authenticateLink = "/authenticate";
        String registerLink = "/people/register";

        http
                .authorizeRequests()
                .antMatchers(authenticateLink, registerLink).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).
                csrf().disable();
    }
}
