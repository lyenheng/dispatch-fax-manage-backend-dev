package com.kedacom.avcs.fax.manage;

import com.kedacom.avcs.dispatchfax.ao.service.impl.DispatchFaxAoClientImpl;
import com.kedacom.avcs.dispatchfax.client.DispatchFaxClient;
import com.kedacom.avcs.dispatchfax.scooper.service.impl.DispatchFaxScooperClientImpl;
import com.kedacom.avcs.dispatchfax.yfl.service.impl.DispatchFaxYflClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author kedacom
 * @date 2018-11-20
 */
@Configuration
@ComponentScan
@EnableJpaAuditing
@EnableJpaRepositories
@EntityScan
public class DispatchFaxManageConfiguration {

    @Value("${dispatch.fax.name:yfl}")
    String faxName;

    @Bean
    public DispatchFaxClient dispatchFaxClient(){
        if(faxName.equals("yfl")){
            return new DispatchFaxYflClientImpl();
        }else if(faxName.equals("ao")){
            return new DispatchFaxAoClientImpl();
        }else if(faxName.equals("scooper")){
            return new DispatchFaxScooperClientImpl();
        }else {
            return null;
        }
    }
}
