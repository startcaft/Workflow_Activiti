package junit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

/**
 * 与用户任务(UserTask)不同的是，接收任务 (ReceiveTask)创建后，会进入一个等待状态，一般指机器自动完成，但需要耗费一定的时间，
 * 当完成工作后，向后推移流程，使用runtimeService.signal(executionId)，传递接收执行对象的id。
 * 
 * 在等待接收任务执行的时候，act_ru_task表中是没有数据的，act_ru_execution表中才有接收任务的数据
 */
public class ReceiveTaskTest {
	
	private ProcessEngine processEngine;
	
	@Before
	public void initEngine(){
		if (processEngine == null) {
			processEngine = ProcessEngines.getDefaultProcessEngine();
		}
	}
	
	/*部署一个流程定义*/
	@Test
	public void deployementFlowTest() {
		
		//部署流程定义
		Deployment deployment = processEngine.getRepositoryService()
								.createDeployment()//创建部署对象
								.addClasspathResource("diagrams/ReceiveTask.bpmn")
								.addClasspathResource("diagrams/ReceiveTask.png")
								.deploy();
		
		//act_re_procdef表中新增一条deployment_id为85001的数据
		System.out.println("流程部署ID：" + deployment.getId());
	}
	
	/*启动一个流程实例*/
	@Test
	public void startProcessTest(){
		
		//根据流程定义图的key来启动一个流程实例【即流程定义图的id属性】
		String processDefinitionKey = "recevieTask";
		
		ProcessInstance processInstance = processEngine.getRuntimeService()
									.startProcessInstanceByKey(processDefinitionKey);
		
		//该流程启动后，执行对象表中act_ru_execution,多了一条proc_def_id为recevieTask:1:85004 , act_id为receiveTask1的记录
		System.out.println("流程实例ID：" + processInstance.getId());
		System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
	}
	
	@Test
	public void queryExecutionTest(){
		
		String processInstanceId = "87501";
		
		Execution execution = processEngine.getRuntimeService()
							.createExecutionQuery()//创建查询执行对象
							.processInstanceId(processInstanceId)//流程实例ID
							.activityId("receivetask1")//当前活动的名称
							.singleResult();
		
		//使用流程变量设置当日的销售额
		processEngine.getRuntimeService().setVariable(execution.getId(), "当日销售额", 20000);
		
		//向后执行一步
		processEngine.getRuntimeService().signal(execution.getId());
		
		//执行对象表中proc_def_id为recevieTask:1:85004的记录的 act_id变为receiveTask2
		//流程变量表act_ru_variable多了一条相应的数据
	}
	
	@Test
	public void testNextExecution(){
		
		String processInstanceId = "87501";
		
		Execution execution = processEngine.getRuntimeService()
							.createExecutionQuery()
							.processInstanceId(processInstanceId)
							.activityId("receivetask2")
							.singleResult();
		
		//获取上一步设置的流程变量，并且给老板发送短信
		Integer value = (Integer) processEngine.getRuntimeService()
					.getVariable(execution.getId(), "当日销售额");
		
		System.out.println("给老板发送短信，内容，当日销售额:" + value + "元");
		
		//再向后一步，该流程就完毕了。
		processEngine.getRuntimeService()
						.signal(execution.getId());
		
		//判断流程是不是结束
		ProcessInstance processInstance = processEngine.getRuntimeService()
									.createProcessInstanceQuery()
									.processInstanceId(processInstanceId)
									.singleResult();
		
		if (processInstance == null) {
			System.out.println("流程结束");
		}
	}
}
