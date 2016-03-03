/**
 * 
 */
package junit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

/*
 * 使用流程变量 指定组任务的办理人。
 * 用法和使用流程变量指定个人任务办理人一样。
 */
public class GroupTaskTwoTest {
	
	private ProcessEngine processEngine;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (processEngine == null) {
			processEngine = ProcessEngines.getDefaultProcessEngine();
		}
	}

	/* 部署流程定义 */
	@Test
	public void testDeployment(){
		
		Deployment deploy = processEngine.getRepositoryService().createDeployment()
									.addClasspathResource("diagrams/GroupTask2.bpmn")
									.addClasspathResource("diagrams/GroupTask2.png")
									.name("分配组任务二")
									.deploy();
		
		System.out.println("流程部署ID：" + deploy.getId());
		System.out.println("流程部署Name：" + deploy.getName());
	}
	
	/* 根据流程定义的key，启动一个流程实例，同时设置流程变量 */
	@Test
	public void testStartProcessInstance(){
		
		String processDefinitionKey = "groupTaskTwo";
		
		/*
		 * 启动流程实例的同时，设置流程变量。
		 * 流程变量的名称，就是在groupTask2.bpmn中定义的activiti:candidateUsers="#{userIds}"中的userIds
		 * 流程变量的值，就是组任务的办理人
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userIds", "AA,BB,CC");
		ProcessInstance pi = processEngine.getRuntimeService()
							.startProcessInstanceByKey(processDefinitionKey, variables);
		
		System.out.println("流程实例ID：" + pi.getId());
		System.out.println("流程定义ID：" + pi.getProcessDefinitionId());
	}
	
	/* 因为分配的是分组任务，而且是直接指定分组办理人，这里使用办理人查询任务是查不到的，是能使用查询组任务 */
	@Test
	public void testFindMyTask(){
		
		String assignee = "AA";
		
		List<Task> list = processEngine.getTaskService()
						.createTaskQuery()
						.taskAssignee(assignee)
						.list();
		
		if (list != null && list.size() > 0) {  
            for (Task task : list) {  
                System.out.println("任务ID：" + task.getId());  
                System.out.println("任务的办理人：" + task.getAssignee());  
                System.out.println("任务名称：" + task.getName());  
                System.out.println("任务的创建时间：" + task.getCreateTime());  
                System.out.println("流程实例ID：" + task.getProcessInstanceId());  
                System.out.println("#######################################");  
            }  
        }  
	}
	
	/*  组任务查询 */
	@Test
	public void testFindGroupTask(){
		
		/* AA,BB,CC   随便哪一个都可以查询到 */
		String groupName = "BB";
		
		
		/* act_ru_identitylink #任务办理表(个人任务，组任务)*/
		/* act_hi_identitylink #历史任务办理表(个人任务，组任务) */
		List<Task> list = processEngine.getTaskService()
							.createTaskQuery()
							.taskCandidateUser(groupName)//参与者，组任务查询
							.list();
		
		if (list != null && list.size() > 0) {  
            for (Task task : list) {  
                System.out.println("任务ID：" + task.getId());  
                System.out.println("任务的办理人：" + task.getAssignee());  
                System.out.println("任务名称：" + task.getName());  
                System.out.println("任务的创建时间：" + task.getCreateTime());  
                System.out.println("流程实例ID：" + task.getProcessInstanceId());  
                System.out.println("#######################################");  
            }  
        }  
	}
	
	/* 查询正在执行的组任务 */
	@Test
	public void findGroupCandidate(){
		
		String taskId = "150005";
		
		/* 通过执行taskId，查询act_ru_identitylink表 */
		List<IdentityLink> identityLinksForTask = processEngine
											.getTaskService()
											.getIdentityLinksForTask(taskId);
		
		if (identityLinksForTask != null && identityLinksForTask.size() > 0) {
			for(IdentityLink indent : identityLinksForTask){
				System.out.println("任务ID：" + indent.getTaskId());
				System.out.println("任务类型：" + indent.getType());
				System.out.println("流程实例ID：" + indent.getProcessInstanceId());
				System.out.println("用户ID：" + indent.getUserId());
				System.out.println("#######################################");  
			}
		}
	}
	
	/* 查询历史的组任务 */
	@Test
	public void findHistoryGroupCandidate(){
		
		/* 通过指定taskId，processInstanceId可以查询历史的组任务，查询出来的数据是不一样的 */
		List<HistoricIdentityLink> lists = processEngine
										.getHistoryService()
										.getHistoricIdentityLinksForProcessInstance("150001");
										//.getHistoricIdentityLinksForTask("150005");
		
		if (lists != null && lists.size() > 0) {  
            for (HistoricIdentityLink identityLink : lists) {  
                System.out.println("任务ID：" + identityLink.getTaskId());  
                System.out.println("流程实例ID："  
                        + identityLink.getProcessInstanceId());  
                System.out.println("用户ID：" + identityLink.getUserId());  
                System.out.println("工作流角色ID：" + identityLink.getGroupId());  
                System.out.println("#########################################");  
            }  
        }  
	}
	
	/* 完成任务 */  
    @Test  
    public void completeTask() {  
    	
        // 任务ID  
        String taskId = "150005";  
        
        processEngine.getTaskService()//  
                .complete(taskId);  
        
        System.out.println("完成任务：" + taskId);  
    } 
}
