package com.kedacom.avcs.fax.manage.data;

import org.springframework.boot.autoconfigure.domain.EntityScan;
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
public class DispatchFaxManageDataConfiguration {

}
