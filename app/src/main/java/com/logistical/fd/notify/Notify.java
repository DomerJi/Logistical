package com.logistical.fd.notify;

/**

 * 通知中心，用来通知更新数据或者UI，采用单例模式

 * @author zhiwu_yan

 * @since 2015年04月6日

 * @version 1.0

 */

public class Notify {

 

    private static volatile Notify mNotify;

    private Notify(){

 

    }

 

    public static Notify getInstance(){

        if(mNotify==null){

            mNotify=new Notify();

        }

        return mNotify;

    }

 

    public void NotifyActivity(String eventType){

        EventSubject eventSubject=EventSubject.getInstance();

        EventType eventTypes=EventType.getInstance();

        if(eventTypes.contains(eventType)){

            eventSubject.notifyObserver(eventType);

        }

    }

}
