package cn.edu.xmu.time.dubbo;

import java.time.LocalDateTime;

public interface TimeService {
    /**
     * 给广告的接口--传一个DateTime，type=0,返回符合这个时间段的广告segId
     * 给商品的接口--传一个DateTime，type=1,返回符合这个时间段的秒杀segId
     * @author hjl
     * @param dateTime 时间
     */
    public Long getTimesegmentIdByTime(LocalDateTime dateTime, Byte type);




    /**
     * 给广告的接口--广告--判断segID是否存在
     * @author hjl
     * @param segID 时段id
     */
    public boolean isTimesegmentIdExist(Long segID) ;



    /**
     * 给商品的接口--根据时间段id获得时间段的bo
     * @author hjl
     * @param segID 时段id
     */
    public String getTimesegmentVoByID(Long segID) ;

}
