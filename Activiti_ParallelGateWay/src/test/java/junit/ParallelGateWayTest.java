package junit;

import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ParallelGateWayTest {
	
	private ProcessEngine engine;

	{
		engine = ProcessEngines.getDefaultProcessEngine();
	}
	
	/** 部署流程定义 **/
	@Test
	public void deploymentTest() {

		Deployment deploy = engine.getRepositoryService().createDeployment()
				.name("并行网关").addClasspathResource("diagrams/parallelGateWay.bpmn")
				.addClasspathResource("diagrams/parallelGateWay.png").deploy();
		
		System.out.println("部署ID：" + deploy.getId());
		System.out.println("部署名称：" + deploy.getName());
	}
	
	/** 启动一个流程实例 **/
	/** 启动流程实例后，act_ru_execution 表中该实例有三条记录，分别是一个流程实例，两个执行对象 **/
	/** act_hi_actinst 表中有四条记录，开始任务，并行网关，付款，发货 这四个活动 **/
	/** act_ru_task 表中有两条正在执行的任务，分别是付款和发货 **/
	/** act_hi_taskinst 历史人物中也有两条任务，分别是付款和发货 **/
	@Test
	public void startProcessInstanceTest(){
		
		String processDefinitionKey = "parallelGateWay";
		
		ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程实例ID：" + processInstance.getId());
		System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
	}
	
	/** 查询当前人的个人任务 **/
	@Test
	public void findMyPersonTask(){
		
		String assignee = "买家";//或卖家
		
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
				System.out.println("流程实例ID：" + task.getProcessInstanceId());
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
		//String taskId = "77507";//付款
		String taskId = "77510";//发货
		
		
		engine.getTaskService()
				.complete(taskId);
		
		System.out.println("完成任务：任务ID：" + taskId);
	}
}
