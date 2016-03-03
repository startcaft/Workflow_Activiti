/**
 * 
 */
package junit;

import java.util.List;

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
 * 直接在流程定义中指定组任务的办理人，这样分配组任务的办理人不够灵活，不要把办理人放置在XML文件中。
 * 
 * act_ru_identitylink #表示正在执行的任务(个人任务，组任务)
 * act_hi_identitylink #表示历史的人物(个人任务，组任务)
 */
public class GroupTaskOneTest {
	
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
									.addClasspathResource("diagrams/GroupTask1.bpmn")
									.addClasspathResource("diagrams/GroupTask1.png")
									.name("分配组任务一")
									.deploy();
		
		System.out.println("流程部署ID：" + deploy.getId());
		System.out.println("流程部署Name：" + deploy.getName());
	}
	
	/* 根据流程定义的key，启动一个流程实例 */
	@Test
	public void testStartProcessInstance(){
		
		String processDefinitionKey = "groupTaskOne";
		
		ProcessInstance pi = processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程实例ID：" + pi.getId());
		System.out.println("流程定义ID：" + pi.getProcessDefinitionId());
	}
	
	/* 因为分配的是分组任务，而且是直接指定分组办理人，这里使用办理人查询任务是查不到的，是能使用查询组任务 */
	@Test
	public void testFindMyTask(){
		
		String assignee = "张三";
		
		List<Task> list = processEngine.getTaskService()
						.createTaskQuery()
						.taskAssignee(assignee)
						.list();
		
		
		/* act_ru_task 表中没有ASSIGNEE_列为"张三"的数据，【act_ru_task #当前正在执行的任务，只有UserTask才会有数据】 */
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
		
		/* 张三,李四,王五   随便哪一个都可以查询到 */
		String groupName = "王五";
		
		
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
		
		String taskId = "140004";
		
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
			}
		}
	}
	
	/* 查询历史的组任务 */
	@Test
	public void findHistoryGroupCandidate(){
		
		/* 通过指定taskId，processInstanceId可以查询历史的组任务，查询出来的数据是不一样的 */
		List<HistoricIdentityLink> lists = processEngine
										.getHistoryService()
										.getHistoricIdentityLinksForProcessInstance("140001");
										//.getHistoricIdentityLinksForTask("140004");
		
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
	
	
	
	/* 拾取任务，将组任务分配给个人任务，指定任务的办理人 */
	@Test
	public void claim(){
		
		String taskId = "140004";
		
		//分配的个人任务(可以是组任务中的成员，也可以是非组任务的成员)
		processEngine.getTaskService().claim(taskId, "startcaft");
		
		//act_ru_task 表中的ASSGINEE_列就有值了。
	}
	
	/* 将个人任务再回退到组任务（前提：之前这个任务是组任务）*/  
    @Test  
    public void setAssignee(){  
        //任务ID  
        String taskId = "140004";  
        
        //之前，一定是一个组任务才能操作
        processEngine.getTaskService()//  
                        .setAssignee(taskId, null);  
        
      //act_ru_task 表中的ASSGINEE_列又为空了
    }  
    
    /* 向组任务中添加成员 */  
    @Test  
    public void addGroupUser(){  
        //任务ID  
        String taskId = "140004";  
        
        //新增组任务的成员  
        String userId = "赵六";  
        
        processEngine.getTaskService()//  
                    .addCandidateUser(taskId, userId);  
    } 
    
    /**向组任务中删除成员*/  
    @Test  
    public void deleteGroupUser(){  
        //任务ID  
        String taskId = "140004";  
        
        //待删除的组任务的成员  
        String userId = "赵六";  
        
        processEngine.getTaskService()//  
                    .deleteCandidateUser(taskId, userId);  
    }  
	
	/* 完成任务 */  
    @Test  
    public void completeTask() {  
    	
        // 任务ID  
        String taskId = "140004";  
        
        processEngine.getTaskService()//  
                .complete(taskId);  
        
        System.out.println("完成任务：" + taskId);  
    }  
}
