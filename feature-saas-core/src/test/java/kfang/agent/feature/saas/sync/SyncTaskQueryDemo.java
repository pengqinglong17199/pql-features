package kfang.agent.feature.saas.sync;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.random.RandomUtil;
import com.alibaba.nacos.common.utils.UuidUtils;
import kfang.agent.feature.saas.thread.synctask.Result;
import kfang.agent.feature.saas.thread.synctask.SyncTask;
import kfang.agent.feature.saas.thread.synctask.TaskEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static kfang.agent.feature.saas.sync.BaoBiaoEnum.*;

/**
 * SyncTaskTest
 *
 * @author pengqinglong
 * @since 2022/4/20
 */
public class SyncTaskQueryDemo {

    public static void main(String[] args) throws Exception {
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < 50; i++) {

            new Thread(() -> {
                count.incrementAndGet();
                try {
                    // list集合中 每个数据 一条sql的查询
                    queryList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count.decrementAndGet();
            }).start();

            new Thread(() -> {
                count.incrementAndGet();
                try {
                    // list集合中 每个数据 一条sql的查询
                    querySingle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count.decrementAndGet();
            }).start();
        }

        for (int i = 0; i < 40; i++) {
            System.out.println(count.get());
            print();
            TimeUnit.SECONDS.sleep(1);
        }
        SyncTask.shutdown();
    }

    private static void print() {
        SyncTask.ConcurrentTaskInfo concurrentTaskInfo = SyncTask.getTaskInfo();
        System.out.println("已完成" + concurrentTaskInfo.getCompleted());
        System.out.println("当前" + concurrentTaskInfo.getCurrent());
        System.out.println("峰值" + concurrentTaskInfo.getMaxPeak());
    }

    private static void queryList() throws Exception {

        List<HouseCount> houseCounts = selectOrgList();

        SyncTask.data(houseCounts)
                // 总量
                .addTask(SUM, SyncTaskQueryDemo::selectHouseSumList, SyncTaskQueryDemo::comparable)
                // 增量
                .addTask(ADD, SyncTaskQueryDemo::selectHouseAddList, SyncTaskQueryDemo::comparable)
                // 7日新增
                .addTask(DAY_7_COUNT, SyncTaskQueryDemo::selectHouseDay7List, SyncTaskQueryDemo::comparable)
                // 执行
                .exec(SyncTaskQueryDemo::consumer);

        //System.out.println(houseCounts);
    }

    private static void querySingle() throws Exception {
        List<HouseCount> houseCounts = selectOrgList();

        SyncTask.data(houseCounts)
                // 总量
                .addTask(SUM, SyncTaskQueryDemo::selectHouseSumSingle)
                // 新增
                .addTask(ADD, SyncTaskQueryDemo::selectHouseAddSingle)
                // 7日新增
                .addTask(DAY_7_COUNT, SyncTaskQueryDemo::selectHouseDay7CountSingle)
                // 执行
                .exec(SyncTaskQueryDemo::consumer);


        //System.out.println(houseCounts);
    }

    private static boolean comparable(HouseCount source, Result result){
        return Objects.equals(source.getOrgId(), result.getPrimaryKey());
    }

    public static List<HouseCount> selectOrgList(){
        List<HouseCount> list = ListUtil.newArrayList();
        // 一页20条
        for (int i = 0; i < 20; i++) {
            list.add(new HouseCount(UuidUtils.generateUuid()));
        }
        return list;
    }

    public static ReportResult selectHouseSumSingle(HouseCount houseCount) {

        // 查询耗时
        sleep(200);

        //System.out.println("查询总数 - select orgId, count(1) from t_sum where org_id = orgId and ... group by orgId");
        return new ReportResult(houseCount.getOrgId(), RandomUtil.randomInt(1, 1000));
    }

    public static ReportResult selectHouseAddSingle(HouseCount houseCount) {

        // 查询耗时 毫秒
        sleep(100);

        //System.out.println("查询新增量 - select orgId, count(1) from t_add where org_id = orgId and ... group by orgId");
        return new ReportResult(houseCount.getOrgId(), RandomUtil.randomInt(1, 1000));
    }

    public static ReportResult selectHouseDay7CountSingle(HouseCount houseCount) {

        // 查询耗时  毫秒
        sleep(300);

        //System.out.println("查询7日新增量 - select orgId, count(1) from t_day_7 where org_id = orgId and ... group by orgId");
        return new ReportResult(houseCount.getOrgId(), RandomUtil.randomInt(1, 1000));
    }

    public static List<ReportResult> selectHouseSumList(List<HouseCount> list) {
        // 查询耗时  毫秒
        sleep(5000);

        //System.out.println("查询总数 - select orgId, count(1) from t_sum where org_id = orgId and ... group by orgId");
        List<ReportResult> results = ListUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            results.add(new ReportResult(list.get(i).getOrgId(), RandomUtil.randomInt(1, 1000)));
        }
        return results;
    }

    public static List<ReportResult> selectHouseAddList(List<HouseCount> list) {
        // 查询耗时  毫秒
        sleep(1000);

        //System.out.println("查询新增量 - select orgId, count(1) from t_add where org_id = orgId and ... group by orgId");
        List<ReportResult> results = ListUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            results.add(new ReportResult(list.get(i).getOrgId(), RandomUtil.randomInt(1, 1000)));
        }
        return results;
    }

    public static List<ReportResult> selectHouseDay7List(List<HouseCount> list) {
        // 查询耗时  毫秒
        sleep(3000);

        //System.out.println("查询7日新增量 - select orgId, count(1) from t_day_7 where org_id = orgId and ... group by orgId");
        List<ReportResult> results = ListUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            results.add(new ReportResult(list.get(i).getOrgId(), RandomUtil.randomInt(1, 1000)));
        }
        return results;
    }

    private static void sleep(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        }catch (Exception e){

        }
    }

    public static void consumer(TaskEvent event, HouseCount source, Result result){
        BaoBiaoEnum baoBiaoEnum = valueOf(event.getEvent());
        switch (baoBiaoEnum){
            case SUM: source.setSum((Integer) result.getResult()); return;
            case ADD: source.setAddCount((Integer) result.getResult()); return;
            case DAY_7_COUNT: source.setDay7Count((Integer) result.getResult()); return;
        }
    }
}

@Getter
@AllArgsConstructor
enum BaoBiaoEnum implements TaskEvent{

    /**
     *
     */
    SUM("总量"),
    ADD("新增量"),
    DAY_7_COUNT("7日新增量"),
    ;


    private final String desc;

    @Override
    public String getEvent() {
        return this.name();
    }
}
@Data
class HouseCount{

    private String orgId;

    private int sum;

    private int addCount;

    private int day7Count;

    public HouseCount(String orgId){
        this.orgId = orgId;
    }
}

@Data
@AllArgsConstructor
class ReportResult implements Result {

    private String orgId;

    private int sum;

    @Override
    public String getPrimaryKey() {
        return orgId;
    }

    @Override
    public Object getResult() {
        return sum;
    }
}