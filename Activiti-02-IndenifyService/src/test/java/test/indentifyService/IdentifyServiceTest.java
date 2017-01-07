package test.indentifyService;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IdentifyServiceTest {
	
	private ProcessEngine processEngine;
	
	@Before
	public void init(){
		//创建流程引擎，使用内存数据库
		processEngine = ProcessEngineConfiguration
					.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
	}
	
	//用户管理API 测试
	@Test
	public void testUser(){
		
		//获取IdentityService实例对象
		IdentityService identityService = processEngine.getIdentityService();
		
		//创建一个用户对象
		User user = identityService.newUser("startcaft");
		user.setFirstName("pi");
		user.setLastName("kai");
		user.setEmail("startcaft@163.com");
		
		//保存用户到数据库
		identityService.saveUser(user);
		//验证用户是否保存成功
		User userInDb = identityService.createUserQuery().userId("startcaft").singleResult();
		Assert.assertNotNull(userInDb);
		
		//删除用户
		identityService.deleteUser("startcaft");
		//验证用户是否删除成功
		userInDb = identityService.createUserQuery().userId("startcaft").singleResult();
		Assert.assertNull(userInDb);
	}
	
	//用户组管理 API 测试
	@Test
	public void testGroup(){
		
		//获取的到IndentityService实例
		IdentityService identityService = processEngine.getIdentityService();
		//创建一个Group对象
		Group group = identityService.newGroup("deptLeader");
		group.setName("部门领导");
		group.setType("assignment");
		
		//保存Group对象
		identityService.saveGroup(group);
		//验证Group是否保存成功
		List<Group> groupList = identityService.createGroupQuery().groupId("deptLeader").list();
		Assert.assertEquals(1, groupList.size());
		
		//删除Group对象
		identityService.deleteGroup("deptLeader");
		//验证删除Group是否成功
		groupList = identityService.createGroupQuery().groupId("deptLeader").list();
		Assert.assertEquals(0, groupList.size());
	}
	
	//用户与组管理 API 测试
	@Test
	public void testUserAndGroupMembership(){
		
		//创建并保存一个Group对象
		IdentityService identityService = processEngine.getIdentityService();
		Group group = identityService.newGroup("deptLeader");
		group.setName("部门领导");
		group.setType("assignment");
		identityService.saveGroup(group);
		
		//创建并保存用户对象
		User user = identityService.newUser("startcaft");
		user.setFirstName("pikai");
		user.setLastName("kai");
		user.setEmail("startcaft@163.com");
		identityService.saveUser(user);
		
		user = identityService.newUser("admin");
		user.setFirstName("zhangsan");
		user.setLastName("san");
		user.setEmail("zhangsan@163.com");
		identityService.saveUser(user);
		
		//将用户startcaft,admin添加到组deptLeader中
		identityService.createMembership("startcaft", "deptLeader");
		identityService.createMembership("admin", "deptLeader");
		
		
		//查询startcaft所属的组
		Group groupContainStartcaft = identityService
					.createGroupQuery().groupMember("startcaft").singleResult();
		Assert.assertNotNull(groupContainStartcaft);
		Assert.assertEquals("deptLeader", groupContainStartcaft.getId());
	}
}
