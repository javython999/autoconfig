package hello.selector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.ObjectInputFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImportSelectorTest {

    @Test
    void staticConfig() {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(StaticConfig.class);
        HelloBean bean = appContext.getBean(HelloBean.class);
        assertNotNull(bean);
    }

    @Test
    void selectorConfig() {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(SelectorConfig.class);
        HelloBean bean = appContext.getBean(HelloBean.class);
        assertNotNull(bean);
    }

    @Configuration
    @Import(HelloConfig.class)
    public static class StaticConfig {}

    @Configuration
    @Import(HelloImportSelector.class)
    public static class SelectorConfig {}



}
