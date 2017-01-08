package test;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.startcaft.activiti.expression.MyBean;

public class ExpressionTest {
	
	private ProcessEngine processEngine;
	
	@Before
	public void inti(){
		//创建流程引擎，使用内存数据库
		processEngine = ProcessEngineConfiguration
					.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
	}
	
	@Test
	public void testExpression() throws Exception{
		//部署流程定义
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		IdentityService indentityService = processEngine.getIdentityService();
		TaskService taskService = processEngine.getTaskService();
		
		repositoryService.createDeployment()
				.addClasspathResource("com/startcaft/activiti/bpmns/expression.bpmn")
				.addClasspathResource("com/startcaft/activiti/bpmns/expression.png")
				.deploy();
		
		
		//将需要的变量初始化
		MyBean myBean = new MyBean();
		Map<String, Object> variables = new HashMap<>();
		variables.put("myBean", myBean);
		String name = "startcaft";
		variables.put("name", name);
		
		//运行期表达式
		//设置流程启动人
		indentityService.setAuthenticatedUserId("pikai");
		String businessKey = "9999";
		//启动一个流程实例，并从第一个ServiceTask中获取authenticatedUserIdForTest变量的值(${authenticatedUserId}表达式的返回值)
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey("expression", businessKey, variables);
		Assert.assertEquals("pikai", runtimeService
										.getVariable(pi.getId(), "authenticatedUserIdForTest"));
		//从第二个ServiceTask中获取returnValue变量的值(${myBean.print(name)}表达式的返回值)
		Assert.assertEquals("startcaft, added by print(String name)", runtimeService
					.getVariable(pi.getId(), "returnValue"));
		//从第三个ServiceTask获取businessKey变量的值(${myBean.printBkey(execution)}表达式的返回值)
		Assert.assertEquals(businessKey, runtimeService
				.getVariable(pi.getId(), "businessKey"));
		
		//用户任务执行，执行任务表达式，调用myBean的方法，在该方法里面设置了一个变量setByTask
		Task task = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).singleResult();
		String setByTask = (String) taskService.getVariable(task.getId(), "setByTask");
		Assert.assertEquals("i'm setted by DelegateTask, " + name, setByTask);
	}
}
