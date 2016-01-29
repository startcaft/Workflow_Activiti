package junit;

import java.util.Date;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import pojo.Person;

/**
 * 流程变量： 在整个工作流中扮演很重要的作用。例如：请假流程中有请假天数，请假原因等一些参数。
 * 流程变量的作用范围只对应一个流程实例，也就是说，各个流程实例的流程变量是互不影响的【例如，张三回老家相亲请假3天，李四新房装修请假15天】。
 * 流程实例结束以后流程变量还保存在数据库中。
 * 
 * 流程变量的作用： 1，用来传递业务参数 2，指定连线完成任务(同意和拒绝) 3，动态指定任务的办理人
 */
public class ProcessVariablesTest {

	private ProcessEngine engine;

	{
		engine = ProcessEngines.getDefaultProcessEngine();
	}

	/** 部署流程定义(InputStream) **/
	@Test
	public void deploymentProcessDefinitionTest() {

		Deployment deploy = engine.getRepositoryService().createDeployment()
				.name("请假流程定义")
				.addClasspathResource("diagrams/processVariables.bpmn")
				.addClasspathResource("diagrams/processVariables.png").deploy();
		
		System.out.println("部署ID：" + deploy.getId());
		System.out.println("部署名称：" + deploy.getName());
	}
	
	
	/** 启动流程实例 **/
	@Test
	public void startProcessInstanceTest(){
		
		String processDefinitionKey = "processVariables";
		ProcessInstance pInstance = engine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程定义ID：" + pInstance.getProcessDefinitionId());
		System.out.println("流程实例ID：" + pInstance.getId());
	}
	
	/** 设置流程变量 **/
	@Test
	public void setProcessVariablesTest(){
		
		TaskService taskService = engine.getTaskService();
		//流程实例
		String processInstanceId = "42501";
		//任务ID
		String currentTaskId = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
		
		/** 一，设置流程变量，使用基本数据类型 **/
//		taskService.setVariableLocal(currentTaskId, "请假天数", 5);							//会和当前任务绑定【其他实例任务无法获取该流程变量】,开发的时候一般不会绑定任务。
//		taskService.setVariable(currentTaskId, "请假日期", new Date());						//没绑定任务【其他实例任务也可以获取该流程变量】
//		taskService.setVariable(currentTaskId, "请假原因", "回家相亲，一起吃饭");					//没绑定任务【其他实例任务也可以获取该流程变量】
		
		/** 二，设置 流程变量，使用JavaBean的方式，一定要实现Serializable接口，而且属性不能再修改，否则抛出反序列化异常 **/
		Person p = new Person();
		p.setId(10);
		p.setName("翠花");
		
		taskService.setVariable(currentTaskId, "人员信息", p);

		
		System.out.println("设置流程变量成功");
	}
	
	/** 获取流程变量 **/
	@Test
	public void getProcessVariablesTest(){
		
		TaskService taskService = engine.getTaskService();
		
		//流程实例
		String processInstanceId = "42501";
		//任务ID
		String currentTaskId = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
		
		/** 一，获取流程变量，使用基本数据类型 **/
//		Integer days = (Integer) taskService.getVariable(currentTaskId, "请假天数");
//		Date date = (Date) taskService.getVariable(currentTaskId, "请假日期");
//		String resean = (String) taskService.getVariable(currentTaskId, "请假原因");
		
//		System.out.println("请假天数：" + days);
//		System.out.println("请假日期：" + date);
//		System.out.println("请假原因：" + resean);
		
		/** 而，获取流程变量，使用JavaBean方式 **/
		Person p = (Person) taskService.getVariable(currentTaskId, "人员信息");
		System.out.println(p.getId() + "-------------------" + p.getName());
		
	}
	
	/**完成任务**/
	@Test
	public void completeTaskTest(){
		
		TaskService taskService = engine.getTaskService();
		
		//流程实例
		String processInstanceId = "27501";
		//任务ID
		String currentTaskId = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();
		
		taskService.complete(currentTaskId);
		
		System.out.println("任务完成");
	}
	
	public void setAndGetProcessVariables(){
		
		/**与流程实例，执行对象(正在执行)相关服务**/
		RuntimeService runtimeService = engine.getRuntimeService();
		/**与任务(正在执行)相关服务**/
		TaskService taskService = engine.getTaskService();
		
		
		/**设置流程变量**/
//		runtimeService.setVariable(executionId, variableName, value);					//表示使用执行对象ID,和流程变量的名称，设置流程变量的值(一次只能设置一个值)
//		runtimeService.setVariables(executionId, variables);							//表示使用执行对象ID,和Map集合设置流程变量，map的key就是流程变量的名称，map的value就是流程变量的值(一次可以设置N个值)
		
//		taskService.setVariable(taskId, variableName, value);							//表示使用任务ID,和流程变量的名称，设置流程变量的值(一次只能设置一个值)
//		taskService.setVariables(taskId, variables);									//表示使用任务ID,和Map集合设置流程变量，Map的key就是流程变量的名称，Map的value就是流程变量的值(一次可以设置N个值)
		
//		runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);		//启动流程实例的时，可以设置流程变量，使用Map集合
		
//		taskService.complete(taskId, variables);										//完成任务的同时，设置流程变量，使用Map集合
		
		
		/**获取流程变量**/
//		runtimeService.getVariable(executionId, variableName);							//使用执行对象ID，流程变量的名称，获取对应流程变量的值
//		runtimeService.getVariables(executionId);										//使用执行对象ID,获取流程变量的Map集合，Map集合的key就是流程变量的名称，Map的value就是流程变量的值
//		runtimeService.getVariables(executionId, variableNames);						//使用执行对象ID，设置流程变量的名称放到一个集合中，获取流程变量的值,返回一个Map集合。
		
//		taskService.getVariable(taskId, variableName);									//使用任务ID和流程变量名，获取流程变量的值
//		taskService.getVariables(taskId);												//使用任务ID，获取流程一个Map集合，Map的key就是流程变量的名称，Map的value就是流程变量的值
//		taskService.getVariables(taskId, variableNames);								//使用任务ID，和流程变量名的集合，获取一个Map，Map的key就是流程变量的名称，Map的value就是流程变量的值
	}
}
