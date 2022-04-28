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
@AllArgsConstructor
public class ErrorTask<T> {

    private TaskEvent event;

    private T source;

    private Exception exception;
}