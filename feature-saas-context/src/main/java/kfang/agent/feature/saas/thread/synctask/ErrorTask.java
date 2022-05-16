package kfang.agent.feature.saas.thread.synctask;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ErrorTask
 *
 * @author pengqinglong
 * @since 2022/4/28
 */
@Data
public class ErrorTask<T> {

    private TaskEvent event;

    private T source;

    private Result result;

    private Exception exception;

    public ErrorTask(TaskEvent event, T source, Exception exception){
        this.event = event;
        this.source = source;
        this.exception = exception;
    }

    public ErrorTask(TaskEvent event, T source, Result result, Exception exception){
        this.event = event;
        this.source = source;
        this.result = result;
        this.exception = exception;
    }
}