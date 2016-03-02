package taskListeners;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerImpl implements TaskListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* 设置任务的办理人(个人任务和组任务) */
	public void notify(DelegateTask delegateTask) {
		
		delegateTask.setAssignee("李四");
	}

}
