package com.zz.upms.admin;

import org.apache.catalina.startup.Tomcat;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.AbstractApplicationContext;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * 自定义动态数据源配置需要排除{@link org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration}，
 * 可以在配置文件排除，或者在这里exclude
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * {@link org.springframework.context.annotation.ComponentScan} 注解只能扫描spring-boot项目包内的bean并注册到spring容器中，
 * 因此需要 EnableAutoConfiguration 注解来注册项目包外的bean。而spring.factories文件，则是用来记录项目包外需要注册的bean类名
 *
 * {@link com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration#sqlSessionFactory(DataSource)} 这里会先创建DataSource Bean，
 * 如果配置了多数据源，则必须要有一个数据源指定@Primary，比如{@link com.zz.upms.admin.config.DataSourceConfig#dataSource(DataSource, DataSource)}动态数据源，
 * 这里优化先初始化需要被注入的ds1,ds2
 *
 * # 执行查询
 * ShardingPreparedStatement#executeQuery
 *
 * {@link java.sql.Connection} 由DataSource的getConnection创建
 * Statement由上面创建的Connection的createStatement
 *
 * ShardingPreparedStatement#execute
 *
 * BasePrepareEngine#prepare#executeRoute
 * DataNodeRouter#route#executeRoute#createRouteContext
 * PreparedQueryPrepareEngine#route
 *
 * SQLParserEngine#parse解析sql,这里有些sql解析就会报错
 * 解析完sql后会调用RouteDecorator根据分表或者读写分离规则注入的代理实现类
 * MySQLParser#parse
 */
//@Import(DataSourceConfig.class)
@MapperScan(basePackages = {"com.zz.upms.base.dao"})
//@NacosPropertySource(dataId = "test", autoRefreshed = true)
@SpringBootApplication(scanBasePackages = {"com.zz.upms"})
@EnableCaching
public class UpmsAdminApplication extends SpringBootServletInitializer {
    /**
     * <h1>Springboot2.2.5 starter自动化配置解析</h1>
     * 在 META-INF/spring.factories 中指定
     * <code>
     * org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
     * com.zz.upms.admin.config.CustomShardingAutoConfiguration
     * </code>
     * 自动配置的类，该类可以使用 @ComponentScan 注解指定扫描包，这样主程序的主函数类中不需要再指定该包。
     *
     * spring-boot-autoconfigure包中的META-INF/spring.factories文件指定了springboot集成的所有自动装配组件。
     *
     * <h1>Springboot2.2.5 启动类的加载</h1>
     * 通过SpringApplication.run启动
     * 1. 调用{@link SpringApplication}构造方法，查找该类所在jar包中的 META-INF/spring.factories 文件配置中的接口实现类，实例化指定接口的实现类，
     * {@link org.springframework.context.ApplicationContextInitializer}, {@link org.springframework.context.ApplicationListener}
     *
     * 2. 调用{@link SpringApplication#run(String...)}，
     * 2.1 调用{@link SpringApplication#getRunListeners}
     * 实例化 SpringApplicationRunListeners接口的实现类{@link org.springframework.boot.context.event.EventPublishingRunListener},
     * 这里会使用"1"实例的listener
     * 2.2 调用{@link SpringApplication#prepareEnvironment}
     * 通过调用注册的 ApplicationListener 监听器的 onApplicationEvent 方法加载解析环境变量的配置文件，
     * {@link org.springframework.boot.context.config.ConfigFileApplicationListener#onApplicationEvent(ApplicationEvent)} ...
     * {@link org.springframework.boot.context.config.ConfigFileApplicationListener#postProcessEnvironment} 解析配置文件以及激活的active profile
     * 2.3 打印banner {@link SpringApplication#printBanner},比如resources目录下的banner.txt文件内容会被打印
     * 2.4 {@link SpringApplication#createApplicationContext} 实例化具体ConfigurableApplicationContext对象
     * {@link org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext}
     * 通过无参构造方法实例化 context 对象
     *
     * 2.5 同样的实例化spring.factories文件中的 SpringBootExceptionReporter 接口的实现类，以2.4的context作为构造方法参数
     * {@link org.springframework.boot.diagnostics.FailureAnalyzers}
     * 2.6 {@link SpringApplication#prepareContext}
     * 为context设置一些属性比如bean name生成器、bean扫描器等，调用ApplicationContextInitializer实现类的initialize方法，
     * 以及ApplicationListener实现类的contextPrepared方法和contextLoaded方法。
     *
     * 2.7 {@link SpringApplication#refreshContext}调用{@link AbstractApplicationContext#refresh()}
     * beanFactory为{@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
     * -> {@link AbstractApplicationContext#invokeBeanFactoryPostProcessors}
     * -> {@link org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory, List)}
     * -> {@link org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry} -> processConfigBeanDefinitions
     * -> {@link org.springframework.context.annotation.ConfigurationClassParser#parse(Set)}
     * -> {@link org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass} -> doProcessConfigurationClass
     * 这里参数 ConfigurationClass 是以主函数的类创建的bean，以及注解信息，会判断 Conditional 条件。
     * 注解可以传递，即注解中引入的注解也会生效
     *
     * 如果有 Component 注解那么会调用{@link org.springframework.context.annotation.ConfigurationClassParser#processMemberClasses}，
     * 该方法会获取声明的成员变量，然后递归调用上面的方法将成员变量创建为 bean
     *
     * <code>
     *     Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
     * 				sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
     * </code>
     * 获取 ComponentScan 注解的所有属性，扫描指定的包并将带有 @Component的类创建为bean，然后再递归创建其属性中的bean，
     * 通过componentScanParser 扫描器扫描指定包下所有带@Component的类，创建的bean存入BeanDefinitionRegistry registry属性中。
     *
     * 2.8 {@link org.springframework.context.annotation.ConfigurationClassParser#processImports}
     * 获取所有 @Import 注解的值，即需要引入的自动配置类(autoConfiguration)，AutoConfigurationImportSelector这个类是关键。
     * 判断导入的类是实现ImportSelector还是实现 ImportBeanDefinitionRegistrar 还是直接创建为Bean，
     * -> {@link org.springframework.boot.autoconfigure.AutoConfigurationImportSelector#selectImports}
     * -> {@link org.springframework.boot.autoconfigure.AutoConfigurationImportSelector#getAutoConfigurationEntry}
     * -> {@link org.springframework.boot.autoconfigure.AutoConfigurationImportSelector#getCandidateConfigurations}
     * 从依赖jar中的 META-INF/spring.factories 文件中获取 {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration} 接口的实现类即自动配置类，
     * 去掉重复的以及指定排除的配置类，
     * 然后又会通过递归循环拆解装配类，
     * 最后会调用{@link org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass} 创建所有的bean
     *
     * <h1>Tomcat启动-基于springboot2.2.5</h1>
     * {@link AbstractApplicationContext#refresh()}
     * 实例化所有的bean之后调用
     * -> {@link AbstractApplicationContext#onRefresh}
     * -> {@link ServletWebServerApplicationContext#onRefresh()}
     * -> {@link ServletWebServerApplicationContext#createWebServer()}
     * 由于是由无参构造方法实例化的对象，因此这里会调用
     * -> {@link ServletWebServerApplicationContext#getWebServerFactory}
     * 从前面创建的bean中获取 {@link org.springframework.boot.web.servlet.server.ServletWebServerFactory} 实例，
     * 该实例会由 spring.factories文件配置的{@link org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration}
     * 自动装配类中使用 @Import 注解引入的容器类
     * {@link org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration}
     * 例如 Tomcat 会实例化为 TomcatServletWebServerFactory，且只能实例化一个，如果同时使用Tomcat和Netty就会报错。
     * 这里注册了一个{@link org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor} bean后置处理器，
     * 其中 {@link org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor#postProcessBeforeInitialization}
     * 在 WebServerFactory Bean初始化前调用，
     * 并调用{@link org.springframework.boot.web.server.WebServerFactoryCustomizer#customize(WebServerFactory)}
     * 接口的所有Bean的customize方法来为ServletWebServerFactory设置参数，比如端口号等
     * 例如{@link org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration#servletWebServerFactoryCustomizer} 创建的Bean，
     * 会设置配置文件中server开头的参数。
     *
     * -> {@link org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory#getWebServer(ServletContextInitializer...)}
     * 创建Tomcat连接，一系列初始化，
     * -> {@link org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory#getTomcatWebServer}
     * 创建 {@link org.springframework.boot.web.embedded.tomcat.TomcatWebServer#initialize}，这里会启动Tomcat进程，监听socket
     * -> {@link Tomcat#start()}
     * -> {@link ServletWebServerApplicationContext#finishRefresh()}
     *
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(UpmsAdminApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UpmsAdminApplication.class);
    }
}
