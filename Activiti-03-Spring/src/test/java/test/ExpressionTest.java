package test;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.startcaft.activiti.expression.MyBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ExpressionTest {
	
	@Autowired
	private ProcessEngineFactoryBean factoryBean;
	
	@Test
	@Deployment(resources="com/startcaft/activiti/bpmns/expression.bpmn")
	public void testExpression() throws Exception{
		
		//将需要的变量初始化
		MyBean myBean = new MyBean();
		Map<String, Object> variables = new HashMap<>();
		variables.put("myBean", myBean);
		String name = "startcaft";
		variables.put("name", name);
		
		//运行期表达式
		//设置流程启动人
		factoryBean.getObject().getIdentityService().setAuthenticatedUserId("pikai");
		String businessKey = "9999";
		//启动一个流程实例，并从第一个ServiceTask中获取authenticatedUserIdForTest变量的值(${authenticatedUserId}表达式的返回值)
		ProcessInstance pi = factoryBean.getObject().getRuntimeService()
				.startProcessInstanceByKey("expression", businessKey, variables);
		Assert.assertEquals("pikai", factoryBean.getObject().getRuntimeService()
										.getVariable(pi.getId(), "authenticatedUserIdForTest"));
		//从第二个ServiceTask中获取returnValue变量的值(${myBean.print(name)}表达式的返回值)
		Assert.assertEquals("startcaft, added by print(String name)", factoryBean.getObject().getRuntimeService()
					.getVariable(pi.getId(), "returnValue"));
		//从第三个ServiceTask获取businessKey变量的值(${myBean.printBkey(execution)}表达式的返回值)
		Assert.assertEquals(businessKey, factoryBean.getObject().getRuntimeService()
				.getVariable(pi.getId(), "businessKey"));
		
		//用户任务执行，执行任务表达式，调用myBean的方法，在该方法里面设置了一个变量setByTask
		Task task = factoryBean.getObject().getTaskService().createTaskQuery()
				.processInstanceId(pi.getId()).singleResult();
		String setByTask = (String) factoryBean.getObject().getTaskService().getVariable(task.getId(), "setByTask");
		Assert.assertEquals("i'm setted by DelegateTask, " + name, setByTask);
	}
}
