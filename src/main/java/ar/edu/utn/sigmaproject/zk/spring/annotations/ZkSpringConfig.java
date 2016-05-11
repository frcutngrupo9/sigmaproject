package ar.edu.utn.sigmaproject.zk.spring.annotations;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ZkSpringConfigRegistrar.class)
public @interface ZkSpringConfig {
}
