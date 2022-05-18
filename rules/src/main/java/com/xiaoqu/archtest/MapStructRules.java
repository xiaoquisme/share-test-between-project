package com.xiaoqu.archtest;

import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.platform.commons.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class MapStructRules {
    @ArchTest
    private final ArchRule mapper_should_be_class =
            classes().that()
                    .areAnnotatedWith(Mapper.class)
                    .should()
                    .notBeInterfaces()
                    .andShould()
                    .beTopLevelClasses();

    @ArchTest
    private final ArchRule mapper_method_should_not_use_expression =
            methods()
                    .that()
                    .areDeclaredInClassesThat()
                    .areAnnotatedWith(Mapper.class)
                    .and()
                    .areAnnotatedWith(Mapping.class)
                    .or()
                    .areAnnotatedWith(Mappings.class)
                    .should()
                    .bePublic()
                    .andShould(notUseExpression())
                    .andShould(beAbstract());


    private ArchCondition<? super JavaMethod> notUseExpression() {
        return new ArchCondition<JavaMethod>("") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                final Set<JavaAnnotation<JavaMethod>> annotations = method.getAnnotations();
                annotations.forEach(annotation -> {
                    if (annotation.getType().getName().equals("org.mapstruct.Mappings")) {
                        @SuppressWarnings("unchecked")
                        JavaAnnotation<JavaMethod>[] mapping = (JavaAnnotation<JavaMethod>[]) annotation.get("value").get();
                        Arrays.stream(mapping).forEach(m -> {
                            final Map<String, Object> properties = m.getProperties();
                            if (StringUtils.isNotBlank(String.valueOf(properties.getOrDefault("expression", "")))) {
                                events.add(SimpleConditionEvent.violated(method, String.format("should use qualifiedByName instead of expression in @Mapping, method name:[%s]", method.getFullName())));
                            }
                        });
                    } else {
                        final Map<String, Object> properties = annotation.getProperties();
                        if (StringUtils.isNotBlank(String.valueOf(properties.getOrDefault("expression", "")))) {
                            events.add(SimpleConditionEvent.violated(method, String.format("should use qualifiedByName instead of expression in @Mapping, method name:[%s]", method.getFullName())));
                        }
                    }
                });
            }
        };
    }

    private ArchCondition<JavaMethod> beAbstract() {
        return new ArchCondition<JavaMethod>("") {
            @Override
            public void check(JavaMethod method, ConditionEvents conditionEvents) {

                if (method.getModifiers().stream().noneMatch(JavaModifier.ABSTRACT::equals)) {
                    conditionEvents.add(SimpleConditionEvent.violated(method, String.format("method [%s] should be abstract", method.getFullName())));
                }
            }
        };
    }

}
