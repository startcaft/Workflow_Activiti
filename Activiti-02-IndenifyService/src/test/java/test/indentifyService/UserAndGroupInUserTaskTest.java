package test.indentifyService;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserAndGroupInUserTaskTest {
	
private ProcessEngine processEngine;
	
	/**
	 * 用户任务候选组的组名
	 */
	private static final String USER_TASK_GROUP_NAME = "deptLeader";
	
	@Before
	public void init(){
		//创建流程引擎，使用内存数据库
		processEngine = ProcessEngineConfiguration
					.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
		
		IdentityService identityService = processEngine.getIdentityService();
		
		//创建用户和组，以及设置它们之间的关系
		Group group = identityService.newGroup(USER_TASK_GROUP_NAME);
		group.setName("领导部门");
		group.setType("assignment");
		identityService.saveGroup(group);
		
		User user = identityService.newUser("startcaft");
		user.setFirstName("pikai");
		user.setLastName("kai");
		user.setEmail("startcaft@163.com");
		identityService.saveUser(user);
		
		identityService.createMembership(user.getId(), group.getId());
	}
	
	@Test
	public void testUserAndGroupInUserTask(){
		//部署流程定义文件.bpmn
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment()
						.addClasspathResource("com/startcaft/activiti/bpmns/userAndGroupInUserTask.bpmn")
						.addClasspathResource("com/startcaft/activiti/bpmns/userAndGroupInUserTask.png")
						.deploy();
		
		//根据流程定义的key来查询部署是否完成
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
										.processDefinitionKey("userAndGroupInUserTask")
										.singleResult();
		Assert.assertNotNull(pd);
		
		//////////////////////////////启动一个流程实例////////////////////////////
		ProcessInstance pi = processEngine.getRuntimeService()
					.startProcessInstanceByKey("userAndGroupInUserTask");
		Assert.assertNotNull(pi);
		
		/**
		 * 解释下[候选人]和[候选组]：
		 * UserTask任务中有两个属性：
		 * 1，activiti:cadidateUsers
		 * 		可以设置多个用户，那么这些用户都属于"候选人"
		 * 
		 * 2，activiti:cadidateGroups
		 * 		可以设置多个组，那么这些组都属于"候选组"，候选组中的用户都可以理解为"候选人"
		 */
		//根据任务候选人查询指定的任务
		Task startcaftTask = processEngine.getTaskService().createTaskQuery()
						.taskCandidateUser("startcaft")
						.singleResult();
		Assert.assertNotNull(startcaftTask);
		
		//手动分配指定任务给指定的人,指定候选人"签收"任务
		processEngine.getTaskService().claim(startcaftTask.getId(), "startcaft");
		//完成任务
		processEngine.getTaskService().complete(startcaftTask.getId());
	}
	
	@After
	public void after(){
		
		//每个测试方法执行完后清理用户与组
		IdentityService identityService = processEngine.getIdentityService();
		
		identityService.deleteMembership("startcaft", USER_TASK_GROUP_NAME);
		identityService.deleteGroup(USER_TASK_GROUP_NAME);
		identityService.deleteUser("startcaft");
	}
}
