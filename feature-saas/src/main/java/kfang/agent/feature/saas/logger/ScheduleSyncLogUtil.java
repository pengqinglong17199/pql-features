package kfang.agent.feature.saas.logger;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.infra.common.cache.CacheOpeType;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.cache.redis.RedisAction;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 定时任务异步日志工具
 *
 * @author pengqinglong
 * @since 2022/1/4
 */
@Data
public class ScheduleSyncLogUtil {

    private static KfangCache agentCache;

    private static ThreadLocal<String> context = new InheritableThreadLocal<>();

    private static final String KEY_PREFIX = "SCHEDULE_SYNC_LOG:";

    private static final String END_PREFIX = "SCHEDULE_SYNC_LOG_END_";

    /**
     * 单例-双重验证
     */
    private static void initCache(){
        if (agentCache == null) {
            synchronized (ScheduleSyncLogUtil.class){
                if(agentCache == null){
                    agentCache = (KfangCache) SpringBeanPicker.getBean("agentCache");
                }
            }
        }
    }

    /**
     * 初始化任务id
     */
    public static String initTask(){

        initCache();

        return UUID.randomUUID().toString();
    }

    public static void setTaskId(String taskId){
        initCache();
        context.set(taskId);
    }

    public static String getTaskId(){
        return context.get();
    }

    /**
     * 保存任务日志
     */
    public static boolean saveLog(String logMessage){
        return saveLog(context.get(), logMessage);
    }

    private static final String SERVICE_AGENT_JMS = "SERVICE_AGENT_JMS";
    /**
     * 保存任务日志
     */
    public static boolean saveLog(String taskId, String logMessage){
        if(StringUtil.isEmpty(taskId)){
            return false;
        }
        String key = SERVICE_AGENT_JMS + KEY_PREFIX + taskId;
        agentCache.<RedisAction>doCustomAction(CacheOpeType.SAVE, redis -> {
            return redis.opsForList().leftPush(key, logMessage);
        });
        return true;
    }

    /**
     * 获取任务日志
     */
    public static List<String> getLogList(){
        return getLogList(context.get());
    }

    /**
     * 获取任务日志
     */
    public static List<String> getLogList(String taskId){

        if(StringUtil.isEmpty(taskId)){
            return ListUtil.newArrayList();
        }

        initCache();

        String key = SERVICE_AGENT_JMS + KEY_PREFIX + taskId;
        return(List<String>)agentCache.<RedisAction>doCustomAction(CacheOpeType.SAVE, redisTemplate -> {
            List<String> temp = ListUtil.newArrayList();
            while (true){
                Object o = redisTemplate.opsForList().rightPop(key);
                if(o == null){
                    break;
                }
                temp.add((String)o);
            }
            return temp;
        });
    }

    /**
     * 获取结束key
     */
    private static String getEndKey(String taskId){
        return END_PREFIX + taskId;
    }

    /**
     * 存入结束
     */
    public static void end(){
        end(context.get());
    }

    /**
     * 存入结束
     */
    public static void end(String taskId){
        saveLog(taskId, getEndKey(taskId));
        context.remove();
    }

    /**
     * 是否结束
     */
    public static boolean isEnd(String taskId, String logMessage){
        return getEndKey(taskId).equals(logMessage);
    }
}