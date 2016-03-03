package taskLisntener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class MyTaskListener implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
     * 可以设置任务的办理人（个人组人和组任务） 
     */  
    public void notify(DelegateTask delegateTask) {  
        //指定组任务  
        delegateTask.addCandidateUser("孙悟空");  
        delegateTask.addCandidateUser("猪八戒");  
        delegateTask.addCandidateUser("唐三藏");
    }  

}
