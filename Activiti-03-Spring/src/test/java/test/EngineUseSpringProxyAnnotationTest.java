package test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class EngineUseSpringProxyAnnotationTest {
	
	@Autowired
	private ProcessEngineFactoryBean factoryBean;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Test
	public void test() throws Exception{
		
		Assert.assertNotNull(runtimeService);
		
		ProcessEngine processEngine = factoryBean.getObject();
		Assert.assertNotNull(processEngine.getRuntimeService());
	}
}
