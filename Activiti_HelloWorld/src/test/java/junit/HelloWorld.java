package junit;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class HelloWorld {
	
	//阅读源代码，可以看到是根据activiti.cfg.xml文件来获取一个ProcessEngine对象。
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**1，部署定义好的流程**/
	@Test
	public void deplymentProcessDefinition(){
		
		Deployment deploy = processEngine.getRepositoryService()	//与流程定义和部署对象相关的Service
						.createDeployment()		//创建一个部署对象
						.name("activiti入门程序")	//添加一个部署的名称
						.addClasspathResource("diagrams/HelloWorld.bpmn")	//从classpath的资源中加载，一次只能加载一个文件
						.addClasspathResource("diagrams/HelloWorld.png")	//从classpath的资源中加载，一次只能加载一个文件
						.deploy();				//完成部署
		
		System.out.println("流程部署ID：" + deploy.getId());
		System.out.println("流程部署名称:" + deploy.getName());
	}
	
	/**2，启动流程实例**/
	@Test
	public void startProcessInstance(){
		
		String processDefinitionKey = "helloWorld";
		
		ProcessInstance proceseeInstance = processEngine.getRuntimeService()		//与正在执行的流程实例和执行对象相关的Service
						.startProcessInstanceByKey(processDefinitionKey);		//使用流程定义的key来启动流程实例，key对应HelloWorld.bpmn文件的id的属性值【用key启动的好处，可以启动最新版本的流程实例(比如去年画了一个流程，今年又画了一个新流程)】
						
		
		System.out.println("流程实例ID：" + proceseeInstance.getId());
		System.out.println("流程定义ID：" + proceseeInstance.getProcessDefinitionId());
		
	}
	
	/**3，查询当前人的个人任务**/
	@Test
	public void findMyPersonTask(){
		
		//String assignee = "张三";
		//String assignee = "李四";
		String assignee = "王五";
		
		List<Task> list = processEngine.getTaskService()			//与正在执行的任务管理相关的Service
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
	
	/**4，完成我的任务**/
	@Test
	public void completeMyPersonTask(){
		
		//String taskId = "5004";
		String taskId = "7502";
		
		processEngine.getTaskService()
						.complete(taskId);		//完成一个任务
		
		System.out.println("完成任务：任务ID：" + taskId);
	}
}
