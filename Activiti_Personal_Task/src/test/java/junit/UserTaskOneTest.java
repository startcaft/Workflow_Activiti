package junit;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

/**
 * 分配个人任务的第一种方式，直接指定办理人，在流程定义中，直接设置UserTask的Assignee属性为办理人，
 * 这样分配任务的办理人不够灵活，因为项目开发中任务的办理人不要放置XML文件中！！！
 */
public class UserTaskOneTest {
	
	private ProcessEngine processEngine;
	
	@Before
	public void initEngine(){
		if (processEngine == null) {
			processEngine = ProcessEngines.getDefaultProcessEngine();
		}
	}
	
	/*部署一个流程定义，并启动该流程的一个实例*/
	@Test
	public void deployementAndStartProcessInstanceTest() {
		
		//部署流程定义
		Deployment deployment = processEngine.getRepositoryService()
								.createDeployment()//创建部署对象
								.addClasspathResource("diagrams/UserTask1.bpmn")
								.addClasspathResource("diagrams/UserTask1.png")
								.deploy();
		
		System.out.println("流程部署ID：" + deployment.getId());
		
		//根据流程定义图的key来启动一个流程实例【即流程定义图的id属性】
		String processDefinitionKey = "userTaskOne";
		
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程实例ID：" + processInstance.getId());
		System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
		
		//act_ru_task表中新增一条流程实例ID为95005的记录。【如果该任务没有分支，则执行id和流程实例ID是一样的】
	}
	
	//查询我的个人任务记录
	@Test
	public void findMyTaskList(){
		
		String user = "张三";
		
		List<Task> list = processEngine.getTaskService()
						.createTaskQuery()
						.taskAssignee(user)//执行个人任务查询
						.list();
		
		for(Task task : list){
			System.out.println("个人任务id=" + task.getId());
			System.out.println("个人任务name=" + task.getName());
			System.out.println("任务办理人assinee="+task.getAssignee());  
			System.out.println("任务创建时间createTime="+task.getCreateTime());  
			System.out.println("任务执行编号executionId="+task.getExecutionId());  
		}
	}
	
	/*完成任务*/
	@Test
	public void completeTaskTest(){
		
		String taskId = "95008";  
	    processEngine.getTaskService()//  
	                .complete(taskId);//  
	    System.out.println("完成任务");  
	    
	    //act_ru_task id为95008的数据消失，act_hi_taskinst表则多了一条id为95008的数据
	}
}
