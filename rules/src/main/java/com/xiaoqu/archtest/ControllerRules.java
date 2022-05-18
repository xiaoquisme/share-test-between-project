package com.xiaoqu.archtest;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class ControllerRules {
    @ArchTest
    private final ArchRule controller_method_should_be_public =
            methods().that()
                    .areMetaAnnotatedWith(RequestMapping.class)
                    .should()
                    .bePublic();
}
