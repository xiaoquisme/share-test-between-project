package com.xiaoqu.archtest;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class ServiceRules {
    @ArchTest
    private final ArchRule test_class_should_be_package_private =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Test")
                    .should()
                    .bePackagePrivate();
    @ArchTest
    private final ArchRule test_method_should_be_package_private =
            methods()
                    .that()
                    .haveNameNotStartingWith("validate_")
                    .and()
                    .areDeclaredInClassesThat()
                    .haveSimpleNameEndingWith("Test")
                    .and()
                    .areAnnotatedWith(Test.class)
                    .or()
                    .areAnnotatedWith(ParameterizedTest.class)
                    .should()
                    .bePackagePrivate();
}
