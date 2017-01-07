package test;

import org.activiti.engine.RuntimeService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EngineUseSpringProxyTest {
	
	private ClassPathXmlApplicationContext context;
	
	@Before
	public void init(){
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	@Test
	public void test(){
		
		//获取引擎工厂
		ProcessEngineFactoryBean factoryBean = 
				context.getBean(ProcessEngineFactoryBean.class);
		//验证获取的工厂对象是否不为空
		Assert.assertNotNull(factoryBean);
		
		//获取runtimeService
		RuntimeService service = context.getBean(RuntimeService.class);
		//验证runtimeService是否不为空
		Assert.assertNotNull(service);
	}
	
	@After
	public void clear(){
		if (context != null) {
			context.close();
		}
	}
}
