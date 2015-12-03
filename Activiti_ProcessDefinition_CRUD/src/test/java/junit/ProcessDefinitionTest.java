package junit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessDefinitionTest {

	private ProcessEngine engine;

	{
		engine = ProcessEngines.getDefaultProcessEngine();
	}

	/** 部署流程定义 **/
	@Test
	public void deploymentTest() {

		Deployment deploy = engine.getRepositoryService().createDeployment()
				.name("流程定义").addClasspathResource("diagrams/HelloWorld.bpmn")
				.addClasspathResource("diagrams/HelloWorld.png").deploy();

		System.out.println("部署ID：" + deploy.getId());
		System.out.println("部署的名称：" + deploy.getName());
	}

	/** 将.bpmn和.png文件打包成.zip格式的文件来部署 **/
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

	/** 查询流程定义 **/
	@Test
	public void findProcessDefinitionTest() {

		List<ProcessDefinition> list = engine.getRepositoryService() // 与流程定义和部署对象相关的服务
				.createProcessDefinitionQuery() // 创建一个流程定义的查询
				/** 指定查询条件，where条件，API很强大啊，自己摸索 **/
				// .deploymentId(deploymentId) //指定部署对象ID查询
				// .processDefinitionKey(processDefinitionKey) //指定流程定义key查询
				// .processDefinitionNameLike(processDefinitionNameLike)
				// //使用流程定义的名称模糊查询
				/** 排序 **/
				.orderByProcessDefinitionVersion().asc() // 按照流程定义的版本升序排序
				// .orderByProcessDefinitionName().desc() //按照流程定义的名称降序排序
				// .count(); //返回结果集数量
				.list(); // 返回一个集合列表，封装流程定义
		// .singleResult(); //返回一个唯一结果集
		// .listPage(firstResult, maxResults) //分页结果集

		if (list != null && list.size() > 0) {
			for (ProcessDefinition processDefinition : list) {

				System.out.println("流程定义ID：" + processDefinition.getId()); // 流程定义的key+版本+随机生成数字，中间用冒号:分隔
				System.out.println("流程定义名称：" + processDefinition.getName()); // 对应HelloWorld.bpmn文件中的name属性值
				System.out.println("流程定义的key值：" + processDefinition.getKey()); // 对应HelloWorld.bpmn文件中的id属性
				System.out.println("流程定义的version值："
						+ processDefinition.getVersion()); // 当流程定义的key值相同时，版本升级，默认1
				System.out.println("资源名称bpmn文件："
						+ processDefinition.getDiagramResourceName());
				System.out.println("资源名称png文件："
						+ processDefinition.getResourceName());
				System.out.println("部署ID："
						+ processDefinition.getDeploymentId());

				System.out
						.println("#######################################################");
			}
		}
	}

	/** 删除流程定义【根据部署ID来删除】 **/
	@Test
	public void deleteProcessDefinitionTest() {

		String deploymentId = "1";
		/**
		 * 非级联删除，如果流程实例已启动，则会报错
		 */
		// engine.getRepositoryService().deleteDeployment(deploymentId);

		/**
		 * 级联删除，不管流程实例是否启动，都可以删除。 项目中一般使用级联删除。
		 */
		engine.getRepositoryService().deleteDeployment(deploymentId, true);

		System.out.println("删除成功");
	}

	/** 删除流程定义【删除key相同的所有不同版本的流程定义】 **/
	@Test
	public void deleteProcessDefinitionByKeyTest() {

		List<ProcessDefinition> list = engine.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionKey("helloworld")
				.orderByProcessDefinitionVersion().asc().list();
		
		if (list != null && list.size() > 0) {
			for (ProcessDefinition pd : list) {
				String deploymentId = pd.getDeploymentId();
				engine.getRepositoryService().deleteDeployment(deploymentId, true);
			}
			System.out.println("删除key相同的所有不同版本的流程定义");
		}
	}

	/**
	 * 查看流程图
	 * 
	 * @throws IOException
	 **/
	@Test
	public void viewPictureTest() throws IOException {

		/** 将生成的图片，放到指定文件夹下 **/
		String deploymentId = "12501";

		// 获取部署对象相关的资源文件名称列表
		List<String> names = engine.getRepositoryService()
				.getDeploymentResourceNames(deploymentId);

		String resourceName = "";
		if (names != null && names.size() > 0) {
			for (String name : names) {
				if (name.indexOf(".png") >= 0) {
					resourceName = name;
				}
			}
		}

		// 获取图片的输入流
		InputStream is = engine.getRepositoryService().getResourceAsStream(
				deploymentId, resourceName);

		// 将图片生成到指定位置
		File file = new File("d:/" + resourceName);
		FileUtils.copyInputStreamToFile(is, file);
	}

	/** 流程定义是不能修改的(流程实例启动了是无法修改的哟，只能升级，按照最新版本的流程定义来执行) **/
	/** 查询最新的流程定义 **/
	@Test
	public void findLastVersionProcessDefinitionTest() {

		List<ProcessDefinition> list = engine.getRepositoryService()
				.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion().asc().list();

		/**
		 * Map<String, ProcessDefinition> map集合的key：流程定义的key map集合的value：流程定义的对象
		 * map集合的特点：当map集合的key值相同的情况下，后一次的值将替换前一次的值
		 */
		Map<String, ProcessDefinition> maps = new HashMap<String, ProcessDefinition>();

		if (list != null && list.size() > 0) {
			for (ProcessDefinition pd : list) {
				maps.put(pd.getKey(), pd);
			}
		}

		List<ProcessDefinition> lastVersionList = new ArrayList<ProcessDefinition>(
				maps.values());

		if (lastVersionList != null && lastVersionList.size() > 0) {
			for (ProcessDefinition processDefinition : lastVersionList) {
				System.out.println("流程定义ID：" + processDefinition.getId());
				System.out.println("流程定义名称：" + processDefinition.getName());
				System.out.println("流程定义的key值：" + processDefinition.getKey());
				System.out.println("流程定义的version值："
						+ processDefinition.getVersion());
				System.out.println("资源名称bpmn文件："
						+ processDefinition.getDiagramResourceName());
				System.out.println("资源名称png文件："
						+ processDefinition.getResourceName());
				System.out.println("部署ID："
						+ processDefinition.getDeploymentId());

				System.out
						.println("#######################################################");
			}
		}
	}
}
