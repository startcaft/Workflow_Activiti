package junit;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * ProcessInstance对象：(流程实例) 代表流程定义的执行实例，一个流程从开始到结束，流程实例只有一个。
 * 
 * Execution对象：(执行对象) 代表流程中的分支，如果只有一个分组，ProcessInstance就是Execution
 * 查看源代码，ProcessInstance是从Execution类继承而来。
 * 
 * 在Activiti的任务中，主要分为两大类查询任务(个人任务和组任务) 1，确切指定了办理者的任务，这个任务将成为办理者的私有任务，即个人任务。
 * 2，无法指定具体的某一个人来办理的任务，可以把任务分配给几个人或者一到多个小组，让这个范围内的用户可以选择来办理任务，即组任务。
 */
public class ProcessInstanceTest {

	private ProcessEngine engine;

	{
		engine = ProcessEngines.getDefaultProcessEngine();
	}

	/** 部署流程定义 **/
	@Test
	public void deploymentZipTest() {

		// 通过类加载器来加载资源文件问一个输入流
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("diagrams/HelloWorld.zip");
		// 通过输入流来创建一个ZipInputStream对象
		ZipInputStream zis = new ZipInputStream(is);

		Deployment deploy = engine.getRepositoryService().createDeployment()
				.name("流程定义").addZipInputStream(zis) // 指定zip格式的文件来完成部署
				.deploy();

		System.out.println("部署ID：" + deploy.getId());
		System.out.println("部署的名称：" + deploy.getName());
	}

	/** 通过RuntimeService来启动一个流程实例 **/
	/** 启动一个流程实例，act_ru_execution表中增加一条记录，act_ru_execution表示正在执行的执行对象表 **/
	/**
	 * 如果是单例流程(没有分支和聚合)，那么流程实例ID和执行对象ID是相同的【流程实例是值流程从开始到结束，它只有一个。
	 * 执行对象就是每个分支就是一个执行对象】
	 **/
	@Test
	public void startProcessInstance() {

		String processDefinitionKey = "helloWorld";
		ProcessInstance processInstance = engine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);

		System.out.println("执行对象ID：" + processInstance.getId());
		System.out.println("流程实例ID：" + processInstance.getId());
		System.out
				.println("流程定义ID：" + processInstance.getProcessDefinitionId());
	}

	/** 查询当前人的个人任务 **/
	@Test
	public void findMyPersonTask() {

		String assignee = "张三";

		List<Task> list = engine.getTaskService() // 与正在执行的任务管理相关的Service
				.createTaskQuery() // 创建任务查询对象
				/** 查询条件(where部分) **/
				.taskAssignee(assignee) // 指定个人任务查询，指定办理人
				// .taskCandidateUser(candidateUser) //组任务的办理人查询
				// .processDefinitionId(processDefinitionId) //使用流程定义的ID查询
				// .processInstanceId(processInstanceId) //使用流程实例ID查询
				/** 排序 **/

				/** 返回结果集 **/
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
				System.out
						.println("#######################################################################");
			}
		}
	}

	/** 完成任务 **/
	@Test
	public void completeMyPersonTask() {

		String taskId = "20004";

		engine.getTaskService().complete(taskId); // 完成一个任务

		System.out.println("完成任务：任务ID：" + taskId);
	}

	/** 查询流程状态(判断流程正在执行，还是已经结束) **/
	/** 在流程执行的过程中，流程实例ID是不会变的，当流程结束后，流程实例将会在正在执行的任务对象表中(act_ru_execution)被删除 **/
	@Test
	public void queryProcessInstanceStateTest() {

		String processInstanceId = "20001";

		ProcessInstance singleResult = engine.getRuntimeService() // 表示正在执行的流程实例
				.createProcessInstanceQuery() // 创建流程实例查询对象
				.processInstanceId(processInstanceId) // 根据流程实例ID查询
				.singleResult();

		if (singleResult != null) {
			System.out.println("流程还在继续");
		} else {
			System.out.println("流程已经结束");
		}
	}

	/** 查询历史任务 **/
	@Test
	public void queryHistoryTaskTest() {

		List<HistoricTaskInstance> list = engine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				/** 查询条件(where部分) **/
				.taskAssignee("张三") // 指定历史任务的办理人
				.list();
		
		if (list != null && list.size() > 0) {
			for (HistoricTaskInstance hTask : list) {
				System.out.println("任务ID：" + hTask.getId());
				System.out.println("流程实例ID：" + hTask.getProcessInstanceId());
				System.out.println("任务名称：" + hTask.getName());
				System.out.println("任务办理人：" + hTask.getAssignee());
				System.out.println("任务开始时间：" + hTask.getCreateTime());
				System.out.println("任务结束时间：" + hTask.getEndTime());
				
				System.out.println("#################################################");
			}
		}
	}
}
