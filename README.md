# 자동 구성(Auto Configuration)
## 스프링 부트와 자동구성
스프링 부트는 자동 구성(AutoConfiguration)이라는 기능을 제공하는데, 일반적으로 자주 사용하는 수 많은 빈들을 자동으로 등록해주는 기능이다.
이러한 자동 구성 덕분에 개발자는 반복적이고 복잡한 빈 등록과 설정을 최소화 하고 애프릴케이션 개발을 빠르게 시작할 수 있다.

스프링 부트가 제공하는 자동 구성
* `https://docs.spring.io/spring-boot/appendix/auto-configuration-classes/index.html`
* 스프링 부트는 수 많은 자동 구성을 제공하고 `spring-boot-autoconfigure`에 자동 구성을 모아 둔다.
* 스프링 부트 프로젝트를 사용하면 `spring-boot-autoconfigure` 라이브러리는 기본적으로 사용된다.

## AutoConfiguration - 용어, 자동 설정? 자동 구성?
Auto Configuration은 주로 다음 두 용어로 번역되어 사용한다.
* 자동 구성
* 자동 설정

자동 설정
`Configuration`이라는 단어가 컴퓨터 용어에서는 환경 설정, 설정이라는 뜻으로 자주 사용된다.
Auto configuration은 크게 보면 빈들을 자동으로 등록해서 스프링이 동작하는 환경을 자동으로 설정해주기 때문에 자동설정이라는 용어도 맞다.

자동 구성
`Configuration`이라는 단어는 구성, 배치라는 뜻도 있다.
예를 들어서 컴퓨터라고 하면 CPU, 메모리 등을 배치해야 컴퓨터가 동작한다. 이렇게 배치하는 것을 구성이라 한다.
스프링도 스프링 실행에 필요한 빈들을 적절하게 배치해야 한다. 자동 구성은 스프링 실행에 필요한 빈들을 자동으로 배치해주는 것이다.

자동 설정, 자동 구성 두 용어 모두 맞는 말이다. 자동 설정은 넓게 사용되는 의미이고, 자동 구성은 실행에 필요한 컴포넌트 조각을 자동으로 배치한다는 더 좁은 의미에 가깝다.

스프링 부트가 제공하는 자동 구성 기능을 이해하려면 다음 두 가지 개념을 이해해야 한다.
* `@Conditional`: 특정 조건에 맞을 때 설정이 동작하도록 한다.
* `@AutoConfiguration`: 자동 구성이 어떻게 동작하는지 내부 원리 이해

## @Conditional
같은 소스 코드인데 특정 상황일 때만 특정 빈들을 등록해서 사용하도록 도와주는 기능이 바로 `@Conditional`이다.
이름 그대로 특정 조건을 만족하는가 하지 않는가를 구별하는 기능이다.
이 기능을 사용하려면 먼저 `Condition` 인터페이스를 구현해야 한다. 그전에 잠깐 `Condition` 인터페이스를 살펴 보자.

Condition

```java
package org.springframework.context.annotation;

import java.lang.reflect.AnnotatedArrayType;

public interface Condition {
    boolean matches(ConditionContext context, AnnotatedArrayType metadata);
}
```
* `matches()` 메서드가 `true`를 반환하면 조건에 만족해서 동작하고 `false`를 반환하면 동작하지 않는다.
* `ConditionContext`: 스프링 컨테이너, 환경 정보등을 담고 있다.
* `AnnotatedArrayType`: 애노테이션 메타 정보를 담고 있다.

`Condition` 인터페이스를 구현해서 다음과 같이 자바 시스템 속성이 `memory=on`이라고 되어 있을 때만 메모리 기능이 동작하도록 만들어 보자.
```properties
#VM Options
#java -Dmemory=on -jar project.jar
```

MemoryCondition
```java
public class MemoryCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String memory = context.getEnvironment().getProperty("memory");
        log.info("memory={}", memory);
        return "on".equals(memory);
    }
 }
```

MemoryConfig
```java
@Configuration
@Conditional(MemoryCondition.class) //추가
public class MemoryConfig {
    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }

    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
```

## Conditional - 다양한 기능
### @ConditionalOnProperty
```java
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {...}
```
* `@ConditionalOnProperty`도 우리가 만든 것과 동일하게 내부에는 `@Conditional`을 사용한다. 그리고 그 안에 `Condition` 인터페이스를 구현한 `OnPropertyCondition`을 가지고 있다.

### @ConditionalOnXxx
스프링은 `@Conditional`과 관련해 개발자가 편리하게 사용할 수 있도록 수 많은 `@ConditionalOnXxx`를 제공한다.
* `@ConditionalOnClass`, `@ConditionalOnMissingClass`
  * 클래스의 존재 여부에 따라 동작한다.
* `@ConditionalOnBean`, `@ConditionalOnMissingBean`
  * 빈이 등록 여부에 따라 동작한다.
* `@ConditionalOnProperty`
  * 환경 정보가 있는 경우 동작한다.
* `@ConditionalOnResource`
  * 리소스가 있는 경우 동작한다.
* `@ConditionalOnWebApplication`, `@ConditionalOnNotWebApplication`
  * 웹 애플리케이션인 여부에 따라 동작한다.
* `@ConditionalOnExpression`
  * SpEL 표현식에 만족한 경우 동작한다.

## 자동 구성 라이브러리
```java
@AutoConfiguration
@ConditionalOnProperty(name = "memory", havingValue = "on")
public class MemoryAutoConfig {
    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }
    @Bean
    
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
 }
```
* `@AutoConfiguration`
  *  스프링 부트가 제공하는 자동 구성 기능을 적용할 때 사용하는 애노테이션이다.
* `@ConditionalOnProperty`
  * `memory=on`이라는 환경 정보가 있을 때 라이브러리를 적용한다.(스프링 빈 등록)
  * 라이브러리를 가지고 있어도 상황에 따라 기능을 켜고 끌 수 있게 유연한 기능을 제공한다.

### 자동 구성 대상 지정
* 스프링 부트 자동 구성을 적용하려면, 다음 파일에 자동 구성 대상을 꼭 지정해주어야 한다.
  * `src/main/resources/META-INF/spring` 경로 생성
  * 생성한 경로에 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` 파일 생성

org.springframework.boot.autoconfigure.AutoConfiguration.imports
```properties
memory.MemoryAutoConfig
```
* 자동 구성인 설정 클래스를 패키지를 포함해서 지정해준다.
* 스프링 부트는 시작 시점에 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`을 읽어서 자동구성으로 사용한다.

스프링 부트 자동 구성이 동작하는 원리는 다음 순서로 확인할 수 있다.
`@SpringBootApplication` -> `@EnableAutoConfiguration` -> `@Import(AutoConfigurationImportSelector.class)`

### ImportSelector
`@Import`에 설정 정보를 추가하는 방법은 2가지가 있다.
* 정적인 방법: `@Import(Example.class)` 처럼 코드에 대상을 지정하는 방식
* 동적인 방법: `@Import(ImportSelector)` 코드로 프로그래밍해서 설정으로 사용할 대상을 동적으로 지정하는 방식

정적인 방법
```java
@Configuration
@Import({AConfig.class, BConfig.class})
public class AppConfig {...}
```

동적인 방법
스프링은 설정 정보 대상을 동적으로 선택할 수 있는 `ImportSelector` 인터페이스를 제공한다.
```java
package org.springframework.context.annotation;
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata importingClassMetadata);
}
```

HelloImportSelector
```java
public class HelloImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"hello.selector.HelloConfig"};
    }
 }
```
* 설정 정보를 동적으로 선택할 수 있게 해주는 `ImportSelector` 인터페이스를 구현했다.
* 여기서는 단순히 `hello.selector.HelloConfig` 설정 정보를 반환한다.
* 이렇게 반환된 정보는 선택되어 사용된다.
* 여기에 설정 정보로 사용할 클래스를 동적으로 프로그래밍 하면된다.