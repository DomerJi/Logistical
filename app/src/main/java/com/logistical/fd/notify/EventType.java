package com.logistical.fd.notify;

import java.util.HashSet;
import java.util.Set;

/**

 * 所有的业务类型，在这里写，方便管理

 * @author zhiwu_yan

 * @since 2015年04月06日

 * @version 1.0

 */

public class EventType {

 

    private static volatile EventType mEventType;

    private final static Set<String> eventsTypes = new HashSet<String>();

 

    public final static String UPDATE_MAIN="com.updateMain";

    public final static String UPDATE_Text="com.updateText";

    public final static String UPDATE_COMPANY ="com.update.company";

    private EventType(){

        eventsTypes.add(UPDATE_MAIN);

        eventsTypes.add(UPDATE_Text);

        eventsTypes.add(UPDATE_COMPANY);

    }

 

    public static EventType getInstance(){

       if(mEventType==null){

           mEventType=new EventType();

       }

        return mEventType;

    }

 

    public boolean contains(String eventType){

        return eventsTypes.contains(eventType);

    }

 

}
