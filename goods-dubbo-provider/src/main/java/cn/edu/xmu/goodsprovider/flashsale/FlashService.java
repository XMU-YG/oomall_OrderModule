package cn.edu.xmu.goodsprovider.flashsale;

public interface FlashService {
//    void loadFlashByTime(Long flashSaleId);

    //4. 将秒杀表中segID等于该时段ID的项的segID置为0
    boolean setFsSegIDZero(Long segId);

}
