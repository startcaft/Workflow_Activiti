package com.startcaft.activiti.test;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;



public class TestActiviti {
	
	@Test
	public void verySimpleLeaveProcessTest(){
		
		//创建流程引擎，使用内存数据库
		ProcessEngine processEngine = ProcessEngineConfiguration
					.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
		
		//部署流程定义文件.bpmn
		RepositoryService repositoryService = processEngine.getRepositoryService();
		repositoryService.createDeployment()
						.addClasspathResource("com/startcaft/activiti/bpmns/leave.bpmn")
						.deploy();
		
		//验证已部署的流程定义文件
		ProcessDefinition prodessDefinition = repositoryService.createProcessDefinitionQuery()
						.singleResult();
		Assert.assertEquals("leave", prodessDefinition.getKey());
		
		//启动流程并返回一个流程实例
		RuntimeService runTimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runTimeService.startProcessInstanceByKey("leave");
		Assert.assertNotNull(processInstance);
		
		System.out.println("pid=" + processInstance.getId() + "\tpdid=" + processInstance.getProcessDefinitionId());
	}
	
	
	@Test
	public void SayHelloToLeaveTest(){
		
		//1,创建流程引擎对象ProcessEngine接口对象
		ProcessEngine engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
		
		//2,获取RepositoryService接口对象,RuntimeService接口对象,TaskSerice接口对象
		RepositoryService rs = engine.getRepositoryService();
		RuntimeService rts = engine.getRuntimeService();
		TaskService ts = engine.getTaskService();
		HistoryService hs = engine.getHistoryService();
		
		String bpmnFileName = "com/startcaft/activiti/bpmns/SayHelloToLeave.bpmn";
		
		//3,部署流程定义文件
		rs.createDeployment().addInputStream(bpmnFileName, this.getClass().getClassLoader()
					.getResourceAsStream(bpmnFileName))
					.deploy();
		
		//4,查询流程定义对象ProcessDefinition接口对象
		ProcessDefinition definition = rs.createProcessDefinitionQuery()
								.singleResult();
		Assert.assertEquals("SayHelloToLeave", definition.getKey());
		
		//5,启动流程(带参数的哦)，并返回一个流程实例对象ProcessInstance接口对象
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("applyUser", "张三");
		params.put("days", 3);
		ProcessInstance pi = rts.startProcessInstanceByKey(definition.getKey(), params);
		Assert.assertNotNull(pi);
		
		System.out.println("pid=" + pi.getId() + "\tpdid=" + pi.getProcessDefinitionId());
		
		
		//6,获取【领导审批】这个组任务
		Task deptLeaderOfTask = ts.createTaskQuery()
				.taskCandidateGroup("deptLeader")
				.singleResult();
		Assert.assertNotNull(deptLeaderOfTask);
		Assert.assertEquals("领导审批", deptLeaderOfTask.getName());
		
		//7,认领任务
		ts.claim(deptLeaderOfTask.getId(), "leaderUser");
		params = new HashMap<String, Object>();
		params.put("approved", true);
		
		//8,完成任务
		ts.complete(deptLeaderOfTask.getId(),params);
		
		//此时，该任务再查询不到了哦
		deptLeaderOfTask = ts.createTaskQuery()
				.taskCandidateGroup("deptLeader")
				.singleResult();
		Assert.assertNull(deptLeaderOfTask);
		
		//9,查询历史(查询流程实例已经完成的个数)
		long count = hs.createHistoricProcessInstanceQuery().finished().count();
		Assert.assertEquals(1, count);
	}
}
