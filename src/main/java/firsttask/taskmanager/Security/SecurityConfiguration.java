package firsttask.taskmanager.Security;


import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl  userDetailsService;
    public SecurityConfiguration(UserDetailsServiceImpl userDetailsService){
        this.userDetailsService=userDetailsService;

    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*super.configure(http);*/
      /*  http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();*/

        http
           .authorizeRequests()   .requestMatchers(EndpointRequest.to("info")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                .antMatchers("/actuator/").hasRole("ADMIN")
                .antMatchers("/users").permitAll()
                .antMatchers("/users/{id}").hasRole("USER")

           .and()
                .logout()

                .and()
                .formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

                auth.userDetailsService(userDetailsService);

    }
}
