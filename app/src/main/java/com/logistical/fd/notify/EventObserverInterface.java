package com.logistical.fd.notify;

/**
 * 观察者接口
 */

public interface EventObserverInterface {

    /**

     * 根据事件进行数据或者UI的更新

     * @param eventType

     */

    public void dispatchChange(String eventType);

}
