package junit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class SequenceFlowTest {

	private ProcessEngine engine;

	{
		engine = ProcessEngines.getDefaultProcessEngine();
	}

	/** 部署流程定义 **/
	@Test
	public void deploymentTest() {

		Deployment deploy = engine.getRepositoryService().createDeployment()
				.name("连线").addClasspathResource("diagrams/sequenceFlow.bpmn")
				.addClasspathResource("diagrams/sequenceFlow.png").deploy();
		
		System.out.println("部署ID：" + deploy.getId());
		System.out.println("部署名称：" + deploy.getName());
	}
	
	/** 启动一个流程实例 **/
	@Test
	public void startProcessInstanceTest(){
		
		String processDefinitionKey = "sequenceFlow";
		
		ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程实例ID：" + processInstance.getId());
		System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
	}
	
	/** 查询当前人的个人任务 **/
	@Test
	public void findMyPersonTask(){
		
		String assignee = "赵六";
		
		List<Task> list = engine.getTaskService()			//与正在执行的任务管理相关的Service
										.createTaskQuery()			//创建任务查询对象
										.taskAssignee(assignee)		//指定个人任务查询，指定办理人
										.list();
		
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务ID：" + task.getId());
				System.out.println("任务名称：" + task.getName());
				System.out.println("任务的创建时间：" + task.getCreateTime());
				System.out.println("任务的办理人：" + task.getAssignee());
				System.out.println("流程实例I：" + task.getProcessInstanceId());
				System.out.println("执行对象ID：" + task.getExecutionId());
				System.out.println("流程定义ID：" + task.getProcessDefinitionId());
				System.out.println("#######################################################################");
			}
		}
	}
	
	/** 完成任务 **/
	@Test
	public void completeMyPersonTask(){
		
		//任务ID
		//String taskId = "52504";
		String taskId = "60004";
		
		//完成任务的同时，设置流程变量，使用流程变量用于指定完成后，下一个连线，对应.bpmn文件中的${message='不重要'}
		Map<String,Object> variables = new HashMap<String, Object>();
		variables.put("message", "重要");
		engine.getTaskService()
				.complete(taskId, variables);
		
		System.out.println("完成任务：任务ID：" + taskId);
	}
}
