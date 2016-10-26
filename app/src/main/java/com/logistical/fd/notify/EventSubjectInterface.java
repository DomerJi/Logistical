package com.logistical.fd.notify;

/**
 * 抽象的主题角色
 */
public interface EventSubjectInterface {

  /**

     * 注册观察者

     * @param observer

     */

    public void registerObserver(String eventType, EventObserver observer);

 

    /**

     * 反注册观察者

     * @param observer

     */

    public void removeObserver(String eventType, EventObserver observer);

 

    /**

     * 通知注册的观察者进行数据或者UI的更新

     */

    public void notifyObserver(String eventType);

}
