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
 * 分配个人任务的第三种方式，使用org.activiti.engine.delegate.TaskListener接口的实现类来配置UserTask的Listeners属性
 * 在启动流程的时候，会自动调用该接口的唯一方法来设置办理人
 */
public class UserTaskThreeTest {

	private ProcessEngine processEngine;

	@Before
	public void initEngine() {
		if (processEngine == null) {
			processEngine = ProcessEngines.getDefaultProcessEngine();
		}
	}

	/* 部署一个流程定义，并启动该流程的一个实例 */
	@Test
	public void deployement() {

		// 部署流程定义
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				// 创建部署对象
				.addClasspathResource("diagrams/UserTask3.bpmn")
				.addClasspathResource("diagrams/UserTask3.png")
				.name("分配个人任务二")
				.deploy();

		System.out.println("流程部署ID：" + deployment.getId());
		System.out.println("流程部署名称：" + deployment.getName());
	}

	@Test
	public void startProcessInstanceTest() {

		// 根据流程定义图的key来启动一个流程实例【即流程定义图的id属性】
		String processDefinitionKey = "userTaskThree";

		//自动调用TaskListener接口的方法来设置办理人
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);

		System.out.println("流程实例ID：" + processInstance.getId());
		System.out
				.println("流程定义ID：" + processInstance.getProcessDefinitionId());
	}

	// 查询我的个人任务记录
	@Test
	public void findMyTaskList() {

		//任务办理人  
        String assignee = "李四";  
        List<Task> list = processEngine.getTaskService()  
                    .createTaskQuery()  
                    .taskAssignee(assignee)//个人任务的查询  
                    .list();  
        
        if(list!=null && list.size()>0){  
            for(Task task:list){  
                System.out.println("任务ID："+task.getId());  
                System.out.println("任务的办理人："+task.getAssignee());  
                System.out.println("任务名称："+task.getName());  
                System.out.println("任务的创建时间："+task.getCreateTime());  
                System.out.println("流程实例ID："+task.getProcessInstanceId());  
                System.out.println("#######################################");  
            }  
        }  
	}

	/* 完成任务 */
	@Test
	public void completeTaskTest() {

		String taskId = "130004";
		processEngine.getTaskService()//130004
				.complete(taskId);//
		
		System.out.println("完成任务");
	}
	
	
	/* 可以分配个人任务从一个人到另外一个任务(认领任务，比如说有人离职了) */
	public void setAssgineeTask(){
		
		String taskId = "130004";
		processEngine.getTaskService()
				.setAssignee(taskId, "灭绝师太");
	}
}
