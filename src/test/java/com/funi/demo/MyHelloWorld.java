package com.funi.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author zhihuan.niu on 8/7/17.
 */
public class MyHelloWorld {

    /**获得流程引擎*/
    private ProcessEngine processEngine(){
        // 引擎配置
        ProcessEngineConfiguration pec=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        pec.setJdbcDriver("com.mysql.jdbc.Driver");
        pec.setJdbcUrl("jdbc:mysql://localhost:3306/db_activiti?useUnicode=true&characterEncoding=utf8");
        pec.setJdbcUsername("activiti");
        pec.setJdbcPassword("Niu*2017");

        /**
         * DB_SCHEMA_UPDATE_FALSE 不能自动创建表，需要表存在
         * create-drop 先删除表再创建表
         * DB_SCHEMA_UPDATE_TRUE 如何表不存在，自动创建和更新表
         */
        pec.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);

        // 获取流程引擎对象
        ProcessEngine processEngine=pec.buildProcessEngine();
        return processEngine;
    }
    /**部署流程定义*/
    @Test
    public void deployProcessDefinition(){
        /*流程定义和流程实例的关系。大家可以把流程定义和流程实例的关系，理解成类和对象的关系；流程定义就是一个模版，流程实例就是通过模版搞出来的具体的可用的东西。*/
        Deployment deployment = processEngine().getRepositoryService()//获取流程定义和部署相关的Service
                .createDeployment()//创建部署对象
                .addClasspathResource("diagrams/MyProcessDemo.bpmn")
                .addClasspathResource("diagrams/MyProcessDemo.png")
                .name("MyProcessDemo")
                .deploy();//完成部署
        /*InputStream inputStream=this.getClass()  // 获取当前class对象
                .getClassLoader()   // 获取类加载器
                .getResourceAsStream("MyProcessDemo.zip"); // 获取指定文件资源流
        ZipInputStream zipInputStream=new ZipInputStream(inputStream); // 实例化zip输入流对象
        // 获取部署对象
        Deployment deployment=processEngine().getRepositoryService() // 部署Service
                .createDeployment()  // 创建部署
                .name("HelloWorld流程2")  // 流程名称
                .addZipInputStream(zipInputStream)  // 添加zip是输入流
                .deploy(); // 部署*/
        System.out.println("部署ID：" + deployment.getId());//部署ID:1
        System.out.println("部署时间：" + deployment.getDeploymentTime());//部署时间

        /*act_re_deployment 部署对象表 存放流程定义的显示名和部署时间，每部署一次增加一条记录*/
        /*act_re_prodef     流程定义表 存放流程定义的属性信息，部署每个新的流程定义都会在这张表中增加一条记录,流程定义id name key version等重要信息*/
        /*act_ge_bytearray  资源文件表 每部署一次就会增加两条记录，一条是关于bpmn规则文件的，一条是图片的*/

    }


    /**附加功能：删除流程定义（删除key相同的所有不同版本的流程定义）*/
    @Test
    public void deleteProcessDefinitionByKey(){
        ProcessEngine processEngine=processEngine();
        //流程定义的key
        String processDefinitionKey = "myProcess";
        //先使用流程定义的key查询流程定义，查询出所有的版本
        List<ProcessDefinition> list = processEngine.getRepositoryService()//
                .createProcessDefinitionQuery()//
                .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
                .list();
        //遍历，获取每个流程定义的部署ID
        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                //获取部署ID
                String deploymentId = pd.getDeploymentId();
                //processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
            }
        }
    }

    /**启动流程实例*/
    @Test
    public void startProcessInstance(){
        ProcessEngine processEngine=processEngine();
        String processDefinitionKey = "myProcess";//流程定义的key,也就是bpmn中存在的ID

        ProcessInstance pi = processEngine.getRuntimeService()//管理流程实例和执行对象，也就是表示正在执行的操作
                .startProcessInstanceByKey(processDefinitionKey);////按照流程定义的key启动流程实例

        System.out.println("流程实例ID：" + pi.getId());//流程实例ID：101
        System.out.println("流程实例ID：" + pi.getProcessInstanceId());//流程实例ID：101
        System.out.println("流程实例ID:" + pi.getProcessDefinitionId());//myMyHelloWorld:1:4

        /*act_ru_task 运行时流程任务表*/
        /*act_ru_execution 运行时流程执行表,存的流程执行相关信息*/
        /*act_ru_identitylink 是于执行主体相关信息表；*/
    }

    /**查看当前任务办理人的个人任务*/
    @Test
    public void findPersonnelTaskList(){
        String assignee = "张三";//当前任务办理人
        String group="部门经理";
        List<Task> tasks = processEngine().getTaskService().createTaskQuery().list();
                //.taskCandidateGroup(group).list();
                //.taskAssignee(assignee).list();
        if(tasks !=null && tasks.size()>0){
            for(Task task:tasks){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务ID:"+task.getId());
                System.out.println("流程实例ID:"+task.getProcessInstanceId());
                System.out.println("#####################################");
            }
        }
    }

    /**完成任务*/
    @Test
    public void completeTask(){
        String taskID = "45005";
        Map<String,Object> map=new HashMap<>();
        map.put("flag",1);
        processEngine().getTaskService().complete(taskID,map);
        System.out.println("完成任务："+taskID);
    /*首先ru开头的运行时候所有表的数据都没了，因为现在流程都走完了。不需要那些数据了；
    然后在hi开头的表里，存了不少数据，主要是用来归档查询用的；*/
    /*act_hi_taskinst 历史流程实例任务表加了一条任务数据；
act_hi_procinst 历史流程实例实例表加了一条流程实例相关信息的数据（包括开始时间，结束时间等等信息）；
act_hi_identitylink 历史流程实例参数者的表加了一条数据；
act_hi_actinst 历史活动节点表加了三条流程活动节点信息的数据（每个流程实例具体的执行活动节点的信息）；*/
    }

    /**设置流程变量*/
    @Test
    public void setVariables(){
        /*在流程执行或者任务执行的过程中，用于设置和获取变量，使用流程变量在流程传递的过程中传递业务参数。
  对应的表：
  act_ru_variable：正在执行的流程变量表
  act_hi_varinst：流程变量历史表*/
        /**与任务（正在执行）*/
        TaskService taskService = processEngine().getTaskService();
        //任务ID
        String taskId = "45005"; //act_ru_task表里id
        /**一：设置流程变量，使用基本数据类型*/
//      taskService.setVariableLocal(taskId, "请假天数", 5);//与任务ID绑定
//      taskService.setVariable(taskId, "请假日期", new Date());
//      taskService.setVariable(taskId, "请假原因", "回家探亲，一起吃个饭");
        /**二：设置流程变量，使用javabean类型*/
        /**
         * 当一个javabean（实现序列号）放置到流程变量中，要求javabean的属性不能再发生变化
         *    * 如果发生变化，再获取的时候，抛出异常
         *
         * 解决方案：在Person对象中添加：
         *      private static final long serialVersionUID = 6757393795687480331L;
         *      同时实现Serializable
         * */
        Person p = new Person();
        p.setId(20);
        p.setName("翠花");
        taskService.setVariable(taskId, "人员信息(添加固定版本)", p);

        System.out.println("设置流程变量成功！");
    }

    /**获取流程变量*/
    @Test
    public void getVariables(){
        /**与任务（正在执行）*/
        TaskService taskService = processEngine().getTaskService();
        //任务ID
        String taskId = "45005";  //act_ru_task表里id
        /**一：获取流程变量，使用基本数据类型*/
//      Integer days = (Integer) taskService.getVariable(taskId, "请假天数");
//      Date date = (Date) taskService.getVariable(taskId, "请假日期");
//      String resean = (String) taskService.getVariable(taskId, "请假原因");
//      System.out.println("请假天数："+days);
//      System.out.println("请假日期："+date);
//      System.out.println("请假原因："+resean);
        /**二：获取流程变量，使用javabean类型*/
        Person p = (Person)taskService.getVariable(taskId, "人员信息(添加固定版本)");
        System.out.println(p.getId()+"        "+p.getName());
    }

    /**查询流程变量的历史表*/
    @Test
    public void findHistoryProcessVariables(){
        List<HistoricVariableInstance> list = processEngine().getHistoryService()//
                .createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询对象
                .variableName("人员信息(添加固定版本)")
                .list();
        if(list!=null && list.size()>0){
            for(HistoricVariableInstance hvi:list){
                System.out.println(hvi.getId()+"   "+hvi.getProcessInstanceId()+"   "+hvi.getVariableName()+"   "+hvi.getVariableTypeName()+"    "+hvi.getValue());
                System.out.println("###############################################");
                Person p = (Person)hvi.getValue();
                System.out.println(p.getId()+"        "+p.getName());
            }
        }
    }

    /**
     * 历史任务查询
     */
    @Test
    public void historyTaskList(){
        List<HistoricTaskInstance> list=processEngine().getHistoryService() // 历史任务Service
                .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
                //.finished() // 查询已经完成的任务
                .processInstanceId("30001")
                .list();
        for(HistoricTaskInstance hti:list){
            System.out.println("任务ID:"+hti.getId());
            System.out.println("流程实例ID:"+hti.getProcessInstanceId());
            System.out.println("办理人："+hti.getAssignee());
            System.out.println("创建时间："+hti.getCreateTime());
            System.out.println("结束时间："+hti.getEndTime());
            System.out.println("===========================");
        }
    }
}
