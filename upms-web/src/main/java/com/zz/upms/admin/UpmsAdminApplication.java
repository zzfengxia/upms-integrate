package com.zz.upms.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.ParserContext;
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
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

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
     *     Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(l
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
    
    /**
     * <span>Spring5.2.x源码解析</span>
     * <h1>XmlBeanFactory创建BeanFactory - 解析xml配置</h1>
     * @see org.springframework.beans.factory.xml.XmlBeanFactory
     * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader#doLoadBeanDefinitions}
     *
     * <h2>加载验证文档数据</h2>
     * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader#doLoadDocument(InputSource, Resource)}
     * 获取文档校验模式，如果有“DOCTYPE”就是DTD,否则是xsd模式，
     * getEntityResolver创建Entity处理器，
     * -> {@link org.springframework.beans.factory.xml.DelegatingEntityResolver#resolveEntity}
     * xsd模式指定 PluggableSchemaResolver 处理
     * -> {@link org.springframework.beans.factory.xml.PluggableSchemaResolver#resolveEntity(String, String)}
     * getSchemaMappings 解析所有META-INF/spring.schemas文件，并缓存
     *
     * -> {@link org.springframework.beans.factory.xml.DefaultDocumentLoader#loadDocument}
     *
     * <h2>解析标签并创建Bean</h2>
     * {@link org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions} 解析xml配置，
     * 先解析profile属性。这里用到了模板方法模式。
     * -> {@link org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseBeanDefinitions} 解析标签创建 BeanDefinition
     * 通过判断文档节点命名空间URI是否有“http://www.springframework.org/schema/beans”判断是默认标签，否则就是自定义。
     * 默认标签：<bean id="springUtils" class="com.vfuchong.sptsm.core.utils.SpringUtils"/>
     * 自定义标签：<tx:annotation-driven/>
     * 解析时每个配置根据规则解析成了一个节点，比如tx就和“xmlns:tx="http://www.springframework.org/schema/tx"”存入一个节点了。
     *
     * <b>解析默认标签</b>
     * -> {@link org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseDefaultElement} 解析配置文件的默认标签
     * import alias bean beans标签
     * -> {@link org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#processBeanDefinition} 实例BeanDefinition，解析xml属性，创建BeanDefinition
     *
     * -> {@link org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element, BeanDefinition)} 解析bean节点属性
     * -> {@link org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element, String, BeanDefinition)}
     * -> {@link org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#parseBeanDefinitionAttributes}
     * lookup-method属性为获取器注入功能，可以为抽象方法指定注入的Bean，实现可拔插的功能。@Lookup注解作用于抽象方法上。
     *
     * -> {@link org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition} 注册BeanDefinition，并注册alias别名与bean的映射关系
     * -> {@link org.springframework.beans.factory.support.DefaultListableBeanFactory#registerBeanDefinition}
     * -> {@link org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry#registerAlias} 注册别名
     *
     * <b>解析自定义标签</b>
     * {@link org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseCustomElement} 解析自定义标签
     * -> {@link org.springframework.beans.factory.xml.BeanDefinitionParserDelegate#parseCustomElement(Element, BeanDefinition)}
     * 先解析配置中的uri namespace
     *
     * -> {@link org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver#resolve}
     * 通过namespace获取对应的Hanlder
     * -> {@link org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver#getHandlerMappings}
     * 从类路径下加载解析所有的META-INF/spring.handlers文件，保存为HandlerMappings，
     * 这里逻辑是如果命名空间对应的handler类还未初始化即还是字符串的话，就使用反射创建对象，并调用init方法，
     * init方法主要是注册标签解析器，比如 {@link org.springframework.transaction.config.TxNamespaceHandler} 中注册annotation-driven标签的解析器
     * 然后将创建出来的handler保缓存进handlerMappings中
     * -> {@link org.springframework.beans.factory.xml.NamespaceHandlerSupport#parse(Element, ParserContext)}
     * 根据标签获取对应的解析器，也就是上面init方法中注册的对应解析器，然后再调用解析器的parse方法注册bean和其他相关操作
     *
     *
     * <h1>Bean的加载</h1>
     * <p>ApplicationContext的加载都会调用{@link AbstractApplicationContext#refresh()}方法，这里会有Bean的加载过程</p>
     * -> finishBeanFactoryInitialization
     * -> {@link org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons}
     * -> getBean
     *
     * 上面是解析在家bean配置，最终得到BeanDefinition。Bean的加载是在调用 getBean 方法阶段完成的。
     * {@link org.springframework.beans.factory.support.AbstractBeanFactory#getBean(Class)}
     * -> {@link org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(String, Class, Object[], boolean)}
     *
     */
}
