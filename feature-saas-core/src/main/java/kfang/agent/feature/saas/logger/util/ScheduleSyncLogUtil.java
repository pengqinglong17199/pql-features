package kfang.agent.feature.saas.logger.util;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import kfang.infra.common.cache.CacheOpeType;
import kfang.infra.common.cache.KfangCache;
import kfang.infra.common.cache.redis.RedisAction;
import kfang.infra.common.spring.SpringBeanPicker;
import lombok.Data;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务异步日志工具
 *
 * @author pengqinglong
 * @since 2022/1/4
 */
@Data
public class ScheduleSyncLogUtil {

    private static ThreadLocal<String> context = new InheritableThreadLocal<>();

    private static final String KEY_PREFIX = ":SCHEDULE_SYNC_LOG:";

    private static final String END_PREFIX = "SCHEDULE_SYNC_LOG_END_";

    private static final class AgentCacheHolder {
        private static final KfangCache AGENT_CACHE = (KfangCache) SpringBeanPicker.getBean("agentCache");
    }

    /**
     * 单例-双重验证
     */
    private static void initCache() {
    }

    /**
     * 初始化任务id
     */
    public static String initTask() {

        initCache();

        return UUID.randomUUID().toString();
    }

    public static void setTaskId(String taskId) {
        initCache();
        context.set(taskId);
    }

    public static String getTaskId() {
        return context.get();
    }

    /**
     * 保存任务日志
     */
    public static boolean saveLog(String logMessage) {
        return saveLog(context.get(), logMessage);
    }

    private static final String SERVICE_AGENT_JMS = "SERVICE_AGENT_JMS";

    /**
     * 保存任务日志
     */
    public static boolean saveLog(String taskId, String logMessage) {
        if (StringUtil.isEmpty(taskId)) {
            return false;
        }

        initCache();

        String key = SERVICE_AGENT_JMS + KEY_PREFIX + taskId;
        AgentCacheHolder.AGENT_CACHE.<RedisAction>doCustomAction(CacheOpeType.SAVE, redis -> {
            Long result = redis.opsForList().leftPush(key, logMessage);
            redis.expire(key, 500, TimeUnit.SECONDS);
            return result;
        });
        return true;
    }

    /**
     * 打印异步处理任务日志
     * 注意 taskId必须真实有效
     */
    public static void printSyncLog(Logger logger, String taskId) throws InterruptedException {

        if (StringUtil.isEmpty(taskId)) {
            return;
        }

        // 防止taskId无效导致的死循环
        int count = 0;

        // 循环一个接口查询是否还有日志
        while (true) {
            List<String> logList = ScheduleSyncLogUtil.getLogList(taskId);

            // 循环100次 500秒 未有日志 认为taskId无效 or 任务失效 跳出死循环 结束任务
            if (ListUtil.isEmpty(logList) && ++count > 100) {
                logger.info("日志打印 等待超时 异常结束");
                return;
            }

            for (String message : logList) {
                // 有日志打印 任务还存活 count归零重新计数
                count = 0;

                // 结束 直接return
                if (ScheduleSyncLogUtil.isEnd(taskId, message)) {
                    logger.info("任务正常结束");
                    return;
                }
                logger.info(message);
            }

            TimeUnit.SECONDS.sleep(5L);
        }
    }

    /**
     * 获取任务日志
     */
    public static List<String> getLogList() {
        return getLogList(context.get());
    }

    /**
     * 获取任务日志
     */
    public static List<String> getLogList(String taskId) {

        if (StringUtil.isEmpty(taskId)) {
            return ListUtil.newArrayList();
        }

        initCache();

        String key = SERVICE_AGENT_JMS + KEY_PREFIX + taskId;
        Object doCustomAction = AgentCacheHolder.AGENT_CACHE.<RedisAction>doCustomAction(CacheOpeType.SAVE, redisTemplate -> {
            List<String> temp = ListUtil.newArrayList();
            while (true) {
                Object o = redisTemplate.opsForList().rightPop(key);
                if (o == null) {
                    break;
                }
                temp.add((String) o);
            }
            return temp;
        });
        return ObjectUtil.cast(doCustomAction);
    }

    /**
     * 获取结束key
     */
    private static String getEndKey(String taskId) {
        return END_PREFIX + taskId;
    }

    /**
     * 存入结束
     */
    public static void end() {
        end(context.get());
    }

    /**
     * 存入结束
     */
    public static void end(String taskId) {
        saveLog(taskId, getEndKey(taskId));
        context.remove();
    }

    /**
     * 是否结束
     */
    public static boolean isEnd(String taskId, String logMessage) {
        return getEndKey(taskId).equals(logMessage);
    }
}