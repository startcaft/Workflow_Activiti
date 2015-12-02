package junit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestActivitiConfig {
	
	/**使用代码创建工作流 所需要的23张表**/
	/**工作流最核心的对象为流程引擎对象：ProcessEngine(任何操作都离不开这个对象)**/
	@Test
	public void createTables(){
		
		ProcessEngineConfiguration pcEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		pcEngine.setJdbcDriver("com.mysql.jdbc.Driver");
		pcEngine.setJdbcUrl("jdbc:mysql://localhost:3306/activiti");
		pcEngine.setJdbcUsername("root");
		pcEngine.setJdbcPassword("5904395");
		
		/**
		 * public static final String DB_SCHEMA_UPDATE_FALSE = "false"					不能自动创建表，需要表存在；
  		 * public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";		先删除表，再创建表；
  		 * public static final String DB_SCHEMA_UPDATE_TRUE = "true"					如果表不存在，自动东创建
		 */
		pcEngine.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		
		//工作流的核心对象：ProcessEngine对象
		ProcessEngine engine = pcEngine.buildProcessEngine();
		
		System.out.println("processEngine:" + engine);
	}
	
	/**根据一个配置文件(activiti.cfg.xml)来创建Activiti所需要的23张表**/
	@Test
	public void createTablesByConfigFile(){
		
		//工作流的核心对象：ProcessEngine对象
		ProcessEngine engine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		
		//ProcessEngine engine = ProcessEngines.getDefaultProcessEngine()
		
		System.out.println("processEngine:" + engine);
	}
	
	/**
	 * 由流程引擎创建各个Service，这些Service是调用工作流23张表的服务。
	 * 
	 * 1，可以产生RepositoryService【管理流程定义】
	 * RepositoryService repositoryService = processEngine.getRepositoryService();
	 * 
	 * 2，可以产生RuntimeService【执行管理，包括启动，推进，删除流程实例等操作】
	 * RuntimeService runTimeService = processEngine.getRuntimeService();
	 * 
	 * 3，可以产生TaskService【任务管理】
	 * TaskService taskService = processEngine.getTaskService();
	 * 
	 * 4，可以产生HistoryService【历史管理(执行完的数据的管理)】
	 * HistoryService historyService = processEngine.getHistoryService();
	 * 
	 * 5，可以产生 FormService【一个可选服务，任务表单管理】
	 * FormService formService = processEngine.getFormService();
	 * 
	 * 6，可以产生ManagerService
	 * ManagerService managerService = processEngine.getManagerService();
	 * 
	 * 7，IdentityService【组织机构管理】
	 * IdentityService identityService = processEngine.getIdentityService();
	 */
}
