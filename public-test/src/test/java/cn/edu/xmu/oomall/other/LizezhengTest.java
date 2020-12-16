package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.transaction.jta.SpringJtaSynchronizationAdapter;


/**
 * 其他模块-广告测试用例
 *
 * @Description 此测试用例需要与时段集成才能跑动
 * @Description 此测试使用以下自己编写的SQL数据：
 * 时段ID:201,202,299
 * 广告ID:1000到1042
 *
 * @author  21620172203354 Zezheng Li
 * @date 2020/12/16 03:25
 */

@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
public class LizezhengTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String token = "";

    static final Long did=0L;

    static final Long unauditID=1000L;

    static final Long onShelvesID=1001L;

    static final Long offShelvesID=1002L;

    static final Long anotherUnauditID=1004L;

    static final Long errorID=1042L;

    static final Long nullSegID=0L;

    static final Long notFullSegID=202L;

    static final Long fullSegID=201L;

    static final Long errorSegID=299L;

    static final String UTF8JSON="application/json;charset=UTF-8";

    static final String notFoundStr="{\"errno\": 504,\"errmsg\": \"操作的资源id不存在\"}";

    static final String SucceededStr="{\"errno\": 0,\"errmsg\": \"成功\"}";

    static final String fieldInvaildStr="{\"errno\": 503,\"errmsg\": \"字段不合法\"}";

    static final String stateForbiddenStr="{\"errno\": 608,\"errmsg\": \"广告状态禁止\"}";

    static final String adFormatErrorStr="{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
    
    static final String updateStr="{\"beginDate\":\"2020-12-10\",\"content\":\"新的内容\",\"endDate\":\"2020-12-10\",\"link\":\"我是一个链接\",\"repeat\":false,\"weight\":\"1123\"}";

    static final String updateWithErrorWeightStr="{\"beginDate\":\"2020-12-10\",\"content\":\"新的内容\",\"endDate\":\"2020-12-10\",\"link\":\"我是一个链接\",\"repeat\":false,\"weight\":\"1233e12\"}";

    static final String updateWithErrorDateStr="{\"beginDate\":\"2020-12-11\",\"content\":\"新的内容\",\"endDate\":\"2020-12-10\",\"link\":\"我是一个链接\",\"repeat\":false,\"weight\":\"123312\"}";

    static final String createNewAdStr=updateStr;

    static final String createNewAdWithErrorDateStr=updateWithErrorDateStr;

    static final String createNewAdWithErrorWeightStr=updateWithErrorWeightStr;

    static final String updateTonullSegmentresult="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":true,\"gmtCreate\":\"null\",\"gmtModified\":\"null\",\"default\":false}}";

    static final String updateToNotFullSegmentresult="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":202,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":true,\"gmtCreate\":\"null\",\"gmtModified\":\"null\",\"default\":false}}";

    static final String auditPassAdvertiseMent="{\"conclusion\":true,\"message\":\"st23ring\"}";

    static final String auditDenyAdvertiseMent="{\"conclusion\":false,\"message\":\"st23ring\"}";

    static final String auditNoConclusionAdvertiseMent="{\"message\":\"st23ring\"}";

    static final String auditNoMessageAdvertiseMent="{\"conclusion\":false}";

    static final String imgSizeOutOfLimit="{\"errno\":509,\"errmsg\":\"图片大小超限\"}";

    static final String getAdsByFullSegId="{\"errno\":0,\"data\":{\"total\":12,\"pages\":1,\"pageSize\":12,\"page\":1,\"list\":[{\"id\":1021,\"link\":\"http://www.baidu21.com\",\"imagePath\":null,\"content\":\"百度广告21\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1022,\"link\":\"http://www.baidu22.com\",\"imagePath\":null,\"content\":\"百度广告22\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1023,\"link\":\"http://www.baidu23.com\",\"imagePath\":null,\"content\":\"百度广告23\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1024,\"link\":\"http://www.baidu24.com\",\"imagePath\":null,\"content\":\"百度广告24\",\"segId\":201,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1025,\"link\":\"http://www.baidu25.com\",\"imagePath\":null,\"content\":\"百度广告25\",\"segId\":201,\"state\":\"下架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1026,\"link\":\"http://www.baidu26.com\",\"imagePath\":null,\"content\":\"百度广告26\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1027,\"link\":\"http://www.baidu27.com\",\"imagePath\":null,\"content\":\"百度广告27\",\"segId\":201,\"state\":\"审核\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1028,\"link\":\"http://www.baidu28.com\",\"imagePath\":null,\"content\":\"百度广告28\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1029,\"link\":\"http://www.baidu29.com\",\"imagePath\":null,\"content\":\"百度广告29\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1030,\"link\":\"http://www.baidu30.com\",\"imagePath\":null,\"content\":\"百度广告30\",\"segId\":201,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1031,\"link\":\"http://www.baidu31.com\",\"imagePath\":null,\"content\":\"百度广告31\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1032,\"link\":\"http://www.baidu32.com\",\"imagePath\":null,\"content\":\"百度广告32\",\"segId\":201,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
    
    static final String getAdsByNotFullSegId="{\"errno\":0,\"data\":{\"total\":9,\"pages\":1,\"pageSize\":9,\"page\":1,\"list\":[{\"id\":1033,\"link\":\"http://www.baidu33.com\",\"imagePath\":null,\"content\":\"百度广告33\",\"segId\":202,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1034,\"link\":\"http://www.baidu34.com\",\"imagePath\":null,\"content\":\"百度广告34\",\"segId\":202,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1035,\"link\":\"http://www.baidu35.com\",\"imagePath\":null,\"content\":\"百度广告35\",\"segId\":202,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1036,\"link\":\"http://www.baidu36.com\",\"imagePath\":null,\"content\":\"百度广告36\",\"segId\":202,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1037,\"link\":\"http://www.baidu37.com\",\"imagePath\":null,\"content\":\"百度广告37\",\"segId\":202,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1038,\"link\":\"http://www.baidu38.com\",\"imagePath\":null,\"content\":\"百度广告38\",\"segId\":202,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1039,\"link\":\"http://www.baidu39.com\",\"imagePath\":null,\"content\":\"百度广告39\",\"segId\":202,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T19:25:55\",\"gmtModified\":null,\"default\":false},{\"id\":1040,\"link\":\"http://www.baidu40.com\",\"imagePath\":null,\"content\":\"百度广告40\",\"segId\":202,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T19:25:55\",\"gmtModified\":null,\"default\":false},{\"id\":1041,\"link\":\"http://www.baidu40.com\",\"imagePath\":null,\"content\":\"百度广告41\",\"segId\":202,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-10-01\",\"endDate\":\"2020-10-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T19:31:20\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";

    static final String getAdsByNullSegId="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";

    static final String getShowAds="{\"errno\":0,\"data\":[{\"id\":1023,\"link\":\"http://www.baidu23.com\",\"imagePath\":null,\"content\":\"百度广告23\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1026,\"link\":\"http://www.baidu26.com\",\"imagePath\":null,\"content\":\"百度广告26\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1032,\"link\":\"http://www.baidu32.com\",\"imagePath\":null,\"content\":\"百度广告32\",\"segId\":201,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1024,\"link\":\"http://www.baidu24.com\",\"imagePath\":null,\"content\":\"百度广告24\",\"segId\":201,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1030,\"link\":\"http://www.baidu30.com\",\"imagePath\":null,\"content\":\"百度广告30\",\"segId\":201,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1029,\"link\":\"http://www.baidu29.com\",\"imagePath\":null,\"content\":\"百度广告29\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1031,\"link\":\"http://www.baidu31.com\",\"imagePath\":null,\"content\":\"百度广告31\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1021,\"link\":\"http://www.baidu21.com\",\"imagePath\":null,\"content\":\"百度广告21\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}],\"errmsg\":\"成功\"}";

    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

   /**
     * 辅助函数：状态测试器：isOK()
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String isOK(ResponseSpec spec) throws Exception {
        byte[] responseString=spec.expectStatus().isOk()
        .expectHeader().contentType(UTF8JSON)
        .expectBody()
        .returnResult().getResponseBodyContent();
        return new String(responseString,"UTF-8");
    }
    /**
     * 辅助函数：状态测试器：isNotFound()
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String isNotFound(ResponseSpec spec) throws Exception {
        byte[] responseString=spec.expectStatus().isNotFound()
        .expectHeader().contentType(UTF8JSON)
        .expectBody()
        .returnResult().getResponseBodyContent();
        return new String(responseString,"UTF-8");
    }
    /**
     * 辅助函数：状态测试器：isBadRequest()
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String isBadRequest(ResponseSpec spec) throws Exception {
        byte[] responseString=spec.expectStatus().isBadRequest()
        .expectHeader().contentType(UTF8JSON)
        .expectBody()
        .returnResult().getResponseBodyContent();
        return new String(responseString,"UTF-8");
    }
    /**
     * 获取该时段下指定tid的广告（第一页，页大小20），一个辅助函数
     * @return ResponseSpec 
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec getAdsBySegIdTestHelper(Long tid) throws Exception {
        String tar="/shops/"+did+"/timesegments/"+tid+"/advertisement?page=1&pageSize=20";
        return mallClient.get().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 获取该时段下指定tid的广告（第一页，页大小20），一个辅助函数
     * @return ResponseSpec
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec getAdsBySegIdTestHelper(Long tid,Integer page,Integer pageSize) throws Exception {
        String tar="/shops/"+did+"/timesegments/"+tid+"/advertisement?page="+page+"&pageSize="+pageSize;
        return mallClient.get().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：删除指定id的广告
     * @return ResponseSpec 
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec deleteAdvertisementHelper(Long id) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id;
        return mallClient.delete().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：设定广告默认值
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec setDefaultAdvertisementHelper(Long id) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id+"/default";
        return mallClient.put().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：设定广告上架
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec onShelvesAdvertisementHelper(Long id) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id+"/onshelves";
        return mallClient.put().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：设定广告下架
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec offShelvesAdvertisementHelper(Long id) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id+"/offshelves";
        return mallClient.put().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：更新广告
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec updateAdvertisementHelper(Long id,String contentJson) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id;
        return mallClient.put().uri(tar).header("authorization",token).bodyValue(contentJson).exchange();
    }
    /**
     * 辅助函数：审核广告
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec auditAdvertisementHelper(Long id,String contentJson) throws Exception{
        String tar="/shops/"+did+"/advertisement/"+id+"/audit";
        return mallClient.put().uri(tar).header("authorization",token).bodyValue(contentJson).exchange();
    }
    /**
     * 辅助函数：移动广告的时间段
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec moveAdtoNewSegTestHelper(Long tid,Long id) throws Exception{
        String tar="/shops/"+did+"/timesegments/"+tid+"/advertisement/"+id;
        return mallClient.put().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：在时段下新建广告
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec createdAdsTestHelper(Long id,String contentJson) throws Exception{
        token="1";
        String tar="/shops/"+did+"/timesegments/"+id+"/advertisement";
        return mallClient.put().uri(tar).header("authorization",token).bodyValue(contentJson).exchange();
    }
    /**
     * 辅助函数：获取广告的所有状态
     * @return ResultActions
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    ResponseSpec getAllAdvertismentStateHelper() throws Exception{
        String tar="/advertisement/states";
        return mallClient.get().uri(tar).header("authorization",token).exchange();
    }
    /**
     * 辅助函数：将JSON中的占位符替换为真正的ID
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String setRealAdID(String JSON,Long realID){
        return JSON.replaceAll("\"id\"\\s*:\\s*\"占位测试，将被新建项的ID替代\"","\"id\": "+realID);
    }
    /**
     * 辅助函数：忽略id字段（将其设为null）
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String setIDToNull(String JSON){
        return JSON.replaceAll("\"id\"\\s*:\\s*[0-9]+","\"id\": null");
    }
    /**
     * 辅助函数：忽略GMTModify字段（将其设为null）
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String setGMTModifyToNull(String JSON){
        return JSON.replaceAll("\"gmtModified\"\\s*:\\s*\"[^\"]*\"","\"gmtModified\": null");
    }
    /**
     * 辅助函数：忽略gmtCreate字段（将其设为null）
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String setgmtCreateToNull(String JSON){
        return JSON.replaceAll("\"gmtCreate\"\\s*:\\s*\"[^\"]*\"","\"gmtCreate\": null");
    }
    /**
     * 辅助函数：忽略errmsg字段（将其设为null）
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    String setErrmsgToNull(String JSON){
        return JSON.replaceAll("\"errmsg\"\\s*:\\s*\"[^\"]*\"","\"errmsg\": null");
    }
    /**
     * 辅助函数：封装JSON比较断言（忽略gmt,errmsg字段）
     * @return String
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    void AssertJSONEqual(String str1,String str2,boolean result) throws Exception
    {   
        String a=setErrmsgToNull(setGMTModifyToNull(setgmtCreateToNull(str1)));
        String b=setErrmsgToNull(setGMTModifyToNull(setgmtCreateToNull(str2)));
        JSONAssert.assertEquals(a,b,result);
    }
    /**
     * 辅助函数：获取JSON的第一个ID字段
     * @return Long
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    Long getID(String JSON) throws Exception
    {
        int index=JSON.indexOf("id");
        while (!Character.isDigit(JSON.charAt(index))) 
            ++index;
        int index2=index;
        while (Character.isDigit(JSON.charAt(index2))) ++index2;
        return Long.parseLong(JSON.substring(index, index2));
    }
    /**
     * 测试1:获取所有的广告状态 
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     * @throws Exception
     */
    @Test
    public void getAllAdvertismentState() throws Exception{
        token = adminLogin("13088admin", "123456");
        String expectedResponse = "{\"errno\": 0,\"data\": [{\"code\": 0,\"stateName\": \"审核\"},{\"code\": 4,\"stateName\": \"上架\"},{\"code\": 6,\"stateName\": \"下架\"}],\"errmsg\": \"成功\"}";
        AssertJSONEqual(expectedResponse, isOK(getAllAdvertismentStateHelper()), true);
    }
    /**
     * 测试2：获取广告时段为201的广告（第二页，页大小为5）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void getAdsBySegIdTest1() throws Exception {
        token = adminLogin("13088admin", "123456");
        String ref="{\"errno\":0,\"data\":{\"total\":12,\"pages\":3,\"pageSize\":5,\"page\":2,\"list\":[{\"id\":1026,\"link\":\"http://www.baidu26.com\",\"imagePath\":null,\"content\":\"百度广告26\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1027,\"link\":\"http://www.baidu27.com\",\"imagePath\":null,\"content\":\"百度广告27\",\"segId\":201,\"state\":\"审核\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1028,\"link\":\"http://www.baidu28.com\",\"imagePath\":null,\"content\":\"百度广告28\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1029,\"link\":\"http://www.baidu29.com\",\"imagePath\":null,\"content\":\"百度广告29\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1030,\"link\":\"http://www.baidu30.com\",\"imagePath\":null,\"content\":\"百度广告30\",\"segId\":201,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(ref, isOK(getAdsBySegIdTestHelper(fullSegID,2,5)), true);
    }
    /**
     * 测试3：获取广告时段202的广告(第三页,最后一页,页大小为4）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void getAdsBySegIdTest2() throws Exception {
        token = adminLogin("13088admin", "123456");
        String ref="{\"errno\":0,\"data\":{\"total\":9,\"pages\":3,\"pageSize\":1,\"page\":3,\"list\":[{\"id\":1041,\"link\":\"http://www.baidu40.com\",\"imagePath\":null,\"content\":\"百度广告41\",\"segId\":202,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-10-01\",\"endDate\":\"2020-10-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T19:31:20\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(ref, isOK(getAdsBySegIdTestHelper(notFullSegID,3,4)), true);
    }
    /**
     * 测试4：获取广告时段202的广告(第四页,空页,页大小为3）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void getAdsBySegIdTest3() throws Exception {
        token = adminLogin("13088admin", "123456");
        String ref="{\"errno\":0,\"data\":{\"total\":9,\"pages\":3,\"pageSize\":0,\"page\":4,\"list\":[]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(ref, isOK(getAdsBySegIdTestHelper(notFullSegID,4,3)), true);
    }
    /**
     * 测试5：获取广告时段0的广告(第一页，页大小20)
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void getAdsBySegIdTest4() throws Exception {
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(getAdsByNullSegId, isOK(getAdsBySegIdTestHelper(nullSegID)), true);
    }
    /**
     * 测试6：获取错误的时段id的广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void getAdsBySegIdTest5() throws Exception {
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(getAdsBySegIdTestHelper(errorSegID)), true);
    }
    /**
     * 测试7：删除未过审广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void deleteAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(deleteAdvertisementHelper(unauditID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":19,\"pages\":1,\"pageSize\":19,\"page\":1,\"list\":[{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试8：删除下架广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void deleteAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(deleteAdvertisementHelper(offShelvesID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":19,\"pages\":1,\"pageSize\":19,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试9：删除上架广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void deleteAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(deleteAdvertisementHelper(onShelvesID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试10：删除不存在的广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void deleteAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(deleteAdvertisementHelper(errorID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    @Test
    public void setDefaultAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(unauditID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T02:06:45\",\"default\":true},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(unauditID)), true);
        AssertJSONEqual(getAdsByNullSegId,setGMTModifyToNull(isOK(getAdsBySegIdTestHelper(nullSegID))),true);
    }
    /**
     * 测试11：设定广告的默认值（错误广告id）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void setDefaultAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(setDefaultAdvertisementHelper(errorID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试12：设定广告的默认值（设定两次，在第二次设定时
     * 观察第一次被设定为默认的广告是否不再是默认广告）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void setDefaultAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(unauditID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T02:06:45\",\"default\":true},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(anotherUnauditID)), true);
        String res2="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T02:06:45\",\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":true},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res2,setGMTModifyToNull(isOK(getAdsBySegIdTestHelper(nullSegID))),true);
    }
    /**
     * 测试13：设定广告的默认值（设定两次，在第二次设定同样的广告）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void setDefaultAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(unauditID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T02:06:45\",\"default\":true},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(SucceededStr, isOK(setDefaultAdvertisementHelper(unauditID)), true);
        AssertJSONEqual(getAdsByNullSegId,setGMTModifyToNull(isOK(getAdsBySegIdTestHelper(nullSegID))),true);
    }

    /**
     * 测试14：设定下架广告上架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void onShelvesAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(onShelvesAdvertisementHelper(offShelvesID)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试15：设定上架广告上架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void onShelvesAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(onShelvesAdvertisementHelper(onShelvesID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试16：设定错误id广告上架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void onShelvesAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(onShelvesAdvertisementHelper(errorID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试17：设定未过审广告上架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void onShelvesAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(onShelvesAdvertisementHelper(onShelvesID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试18：设定上架广告下架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void offShelvesAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(offShelvesAdvertisementHelper(unauditID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试19：设定下架广告下架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void offShelvesAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(offShelvesAdvertisementHelper(offShelvesID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试20：设定错误id广告下架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void offShelvesAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(offShelvesAdvertisementHelper(errorID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试21：设定未过审广告下架
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void offShelvesAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(offShelvesAdvertisementHelper(unauditID)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试22：更新下架广告(状态禁止)
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(updateAdvertisementHelper(offShelvesID,updateStr)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试23：更新上架广告(状态禁止)
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(updateAdvertisementHelper(onShelvesID,updateStr)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试24：更新未过审广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(updateAdvertisementHelper(unauditID,updateStr)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"我是一个链接\",\"imagePath\":null,\"content\":\"新的内容\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1123\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-10\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:21:41\",\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试25：更新不存在的广告：资源不存在
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(updateAdvertisementHelper(errorID,updateStr)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试26：开始日期晚于结束日期：参数不合法
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement5() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(fieldInvaildStr, isBadRequest(updateAdvertisementHelper(unauditID,updateWithErrorDateStr)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试27：weight不合法（不是数字）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void updateAdvertisement6() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(fieldInvaildStr, isBadRequest(updateAdvertisementHelper(unauditID,updateWithErrorWeightStr)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试28：审核未过审广告：结论为通过
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement1() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(auditAdvertisementHelper(unauditID,auditPassAdvertiseMent)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"下架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:55\",\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试29：审核未过审广告：结论为未通过
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(SucceededStr, isOK(auditAdvertisementHelper(unauditID,auditDenyAdvertiseMent)), true);
        String res="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":0,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:57:26\",\"default\":false},{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试30：审核上架广告，结论为真：状态不合法
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(auditAdvertisementHelper(onShelvesID,auditPassAdvertiseMent)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试31：审核下架广告，结论为真，状态不合法
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(auditAdvertisementHelper(offShelvesID,auditPassAdvertiseMent)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试32：审核上架广告，结论为假：状态不合法
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement5() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(auditAdvertisementHelper(onShelvesID,auditDenyAdvertiseMent)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试33：审核下架广告，结论为假，状态不合法
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement6() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(stateForbiddenStr, isOK(auditAdvertisementHelper(offShelvesID,auditDenyAdvertiseMent)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试34：审核不存在的广告id
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement7() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(auditAdvertisementHelper(errorID,auditDenyAdvertiseMent)), true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试35：conclusion字段不能为null
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement8() throws Exception{
        token = adminLogin("13088admin", "123456");
        isBadRequest(auditAdvertisementHelper(unauditID,auditNoConclusionAdvertiseMent));
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试36：message字段不能为null
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void auditAdvertisement9() throws Exception{
        token = adminLogin("13088admin", "123456");
        isBadRequest(auditAdvertisementHelper(unauditID,auditNoMessageAdvertiseMent));
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }
    /**
     * 测试37：正常移动广告时间段
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void moveAdtoNewSegTest1() throws Exception{
        token = adminLogin("13088admin", "123456");
        String res0="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":201,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-15T21:50:57\",\"default\":false}}";
        AssertJSONEqual(res0, isOK(moveAdtoNewSegTestHelper(fullSegID,unauditID)),true);
        String res1="{\"errno\":0,\"data\":{\"total\":19,\"pages\":1,\"pageSize\":19,\"page\":1,\"list\":[{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        String res2="{\"errno\":0,\"data\":{\"total\":13,\"pages\":1,\"pageSize\":13,\"page\":1,\"list\":[{\"id\":1000,\"link\":\"http://www.baidu00.com\",\"imagePath\":null,\"content\":\"百度广告00\",\"segId\":201,\"state\":\"审核\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T02:00:26\",\"default\":false},{\"id\":1021,\"link\":\"http://www.baidu21.com\",\"imagePath\":null,\"content\":\"百度广告21\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1022,\"link\":\"http://www.baidu22.com\",\"imagePath\":null,\"content\":\"百度广告22\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1023,\"link\":\"http://www.baidu23.com\",\"imagePath\":null,\"content\":\"百度广告23\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1024,\"link\":\"http://www.baidu24.com\",\"imagePath\":null,\"content\":\"百度广告24\",\"segId\":201,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1025,\"link\":\"http://www.baidu25.com\",\"imagePath\":null,\"content\":\"百度广告25\",\"segId\":201,\"state\":\"下架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1026,\"link\":\"http://www.baidu26.com\",\"imagePath\":null,\"content\":\"百度广告26\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1027,\"link\":\"http://www.baidu27.com\",\"imagePath\":null,\"content\":\"百度广告27\",\"segId\":201,\"state\":\"审核\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1028,\"link\":\"http://www.baidu28.com\",\"imagePath\":null,\"content\":\"百度广告28\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1029,\"link\":\"http://www.baidu29.com\",\"imagePath\":null,\"content\":\"百度广告29\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1030,\"link\":\"http://www.baidu30.com\",\"imagePath\":null,\"content\":\"百度广告30\",\"segId\":201,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1031,\"link\":\"http://www.baidu31.com\",\"imagePath\":null,\"content\":\"百度广告31\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1032,\"link\":\"http://www.baidu32.com\",\"imagePath\":null,\"content\":\"百度广告32\",\"segId\":201,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(res1,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(res2,isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }
    /**
     * 测试38：移动不存在的广告id
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void moveAdtoNewSegTest2() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(moveAdtoNewSegTestHelper(fullSegID,errorID)),true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(getAdsByFullSegId,isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }
    /**
     * 测试39：移动不存在的时间段id
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void moveAdtoNewSegTest3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(moveAdtoNewSegTestHelper(errorSegID,unauditID)),true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
        AssertJSONEqual(getAdsByFullSegId,isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }
    /**
     * 测试40：在非0时段下新建广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void createdAdsTest1() throws Exception{
        token = adminLogin("13088admin", "123456");
        String res0="{\"errno\":0,\"data\":{\"id\":1234,\"link\":\"我是一个链接\",\"imagePath\":null,\"content\":\"新的内容\",\"segId\":201,\"state\":\"审核\",\"weight\":\"1123\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-10\",\"repeat\":false,\"gmtCreate\":\"2020-12-15T22:36:16.804499900\",\"gmtModified\":null,\"default\":false},\"errmsg\":\"成功\"}";
        String res1=isOK(createdAdsTestHelper(fullSegID,createNewAdStr));
        String res2="{\"errno\":0,\"data\":{\"total\":13,\"pages\":1,\"pageSize\":13,\"page\":1,\"list\":[{\"id\":1021,\"link\":\"http://www.baidu21.com\",\"imagePath\":null,\"content\":\"百度广告21\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1022,\"link\":\"http://www.baidu22.com\",\"imagePath\":null,\"content\":\"百度广告22\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1023,\"link\":\"http://www.baidu23.com\",\"imagePath\":null,\"content\":\"百度广告23\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1024,\"link\":\"http://www.baidu24.com\",\"imagePath\":null,\"content\":\"百度广告24\",\"segId\":201,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1025,\"link\":\"http://www.baidu25.com\",\"imagePath\":null,\"content\":\"百度广告25\",\"segId\":201,\"state\":\"下架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1026,\"link\":\"http://www.baidu26.com\",\"imagePath\":null,\"content\":\"百度广告26\",\"segId\":201,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1027,\"link\":\"http://www.baidu27.com\",\"imagePath\":null,\"content\":\"百度广告27\",\"segId\":201,\"state\":\"审核\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1028,\"link\":\"http://www.baidu28.com\",\"imagePath\":null,\"content\":\"百度广告28\",\"segId\":201,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1029,\"link\":\"http://www.baidu29.com\",\"imagePath\":null,\"content\":\"百度广告29\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1030,\"link\":\"http://www.baidu30.com\",\"imagePath\":null,\"content\":\"百度广告30\",\"segId\":201,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1031,\"link\":\"http://www.baidu31.com\",\"imagePath\":null,\"content\":\"百度广告31\",\"segId\":201,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":1032,\"link\":\"http://www.baidu32.com\",\"imagePath\":null,\"content\":\"百度广告32\",\"segId\":201,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:41\",\"gmtModified\":null,\"default\":false},{\"id\":\"占位测试，将被新建项的ID替代\",\"link\":\"我是一个链接\",\"imagePath\":null,\"content\":\"新的内容\",\"segId\":201,\"state\":\"审核\",\"weight\":\"1123\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-10\",\"repeat\":false,\"gmtCreate\":\"2020-12-16T01:32:55\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(setIDToNull(res0),setIDToNull(res1),true);
        Long realID=getID(res1);
        AssertJSONEqual(setRealAdID(res2, realID),isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }

    /**
     * 测试41：在空时段（0）下新建广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void createdAdsTest2() throws Exception{
        deleteAdvertisementHelper(unauditID);
        token = adminLogin("13088admin", "123456");
        String res0="{\"errno\":0,\"data\":{\"id\":1234,\"link\":\"我是一个链接\",\"imagePath\":null,\"content\":\"新的内容\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1123\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-10\",\"repeat\":false,\"gmtCreate\":\"2020-12-15T22:52:25.646001400\",\"gmtModified\":null,\"default\":false},\"errmsg\":\"成功\"}";
        String res1=isOK(createdAdsTestHelper(nullSegID,createNewAdStr));
        String res2="{\"errno\":0,\"data\":{\"total\":20,\"pages\":1,\"pageSize\":20,\"page\":1,\"list\":[{\"id\":1001,\"link\":\"http://www.baidu01.com\",\"imagePath\":null,\"content\":\"百度广告01\",\"segId\":0,\"state\":\"上架\",\"weight\":\"1\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1002,\"link\":\"http://www.baidu02.com\",\"imagePath\":null,\"content\":\"百度广告02\",\"segId\":0,\"state\":\"下架\",\"weight\":\"2\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":\"2020-12-16T01:15:37\",\"default\":false},{\"id\":1003,\"link\":\"http://www.baidu03.com\",\"imagePath\":null,\"content\":\"百度广告03\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1004,\"link\":\"http://www.baidu04.com\",\"imagePath\":null,\"content\":\"百度广告04\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1005,\"link\":\"http://www.baidu05.com\",\"imagePath\":null,\"content\":\"百度广告05\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1006,\"link\":\"http://www.baidu06.com\",\"imagePath\":null,\"content\":\"百度广告06\",\"segId\":0,\"state\":\"下架\",\"weight\":\"6\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1007,\"link\":\"http://www.baidu07.com\",\"imagePath\":null,\"content\":\"百度广告07\",\"segId\":0,\"state\":\"上架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1008,\"link\":\"http://www.baidu08.com\",\"imagePath\":null,\"content\":\"百度广告08\",\"segId\":0,\"state\":\"审核\",\"weight\":\"4\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1009,\"link\":\"http://www.baidu09.com\",\"imagePath\":null,\"content\":\"百度广告09\",\"segId\":0,\"state\":\"上架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1010,\"link\":\"http://www.baidu10.com\",\"imagePath\":null,\"content\":\"百度广告10\",\"segId\":0,\"state\":\"下架\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1011,\"link\":\"http://www.baidu11.com\",\"imagePath\":null,\"content\":\"百度广告11\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1012,\"link\":\"http://www.baidu12.com\",\"imagePath\":null,\"content\":\"百度广告12\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1013,\"link\":\"http://www.baidu13.com\",\"imagePath\":null,\"content\":\"百度广告13\",\"segId\":0,\"state\":\"上架\",\"weight\":\"0\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1014,\"link\":\"http://www.baidu14.com\",\"imagePath\":null,\"content\":\"百度广告14\",\"segId\":0,\"state\":\"下架\",\"weight\":\"1\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1015,\"link\":\"http://www.baidu15.com\",\"imagePath\":null,\"content\":\"百度广告15\",\"segId\":0,\"state\":\"上架\",\"weight\":\"2\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1016,\"link\":\"http://www.baidu16.com\",\"imagePath\":null,\"content\":\"百度广告16\",\"segId\":0,\"state\":\"审核\",\"weight\":\"3\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1017,\"link\":\"http://www.baidu17.com\",\"imagePath\":null,\"content\":\"百度广告17\",\"segId\":0,\"state\":\"上架\",\"weight\":\"4\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1018,\"link\":\"http://www.baidu18.com\",\"imagePath\":null,\"content\":\"百度广告18\",\"segId\":0,\"state\":\"下架\",\"weight\":\"5\",\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":1019,\"link\":\"http://www.baidu19.com\",\"imagePath\":null,\"content\":\"百度广告19\",\"segId\":0,\"state\":\"审核\",\"weight\":\"6\",\"beginDate\":\"2020-11-01\",\"endDate\":\"2020-11-30\",\"repeat\":false,\"gmtCreate\":\"2020-12-14T18:07:40\",\"gmtModified\":null,\"default\":false},{\"id\":\"占位测试，将被新建项的ID替代\",\"link\":\"我是一个链接\",\"imagePath\":null,\"content\":\"新的内容\",\"segId\":0,\"state\":\"审核\",\"weight\":\"1123\",\"beginDate\":\"2020-12-10\",\"endDate\":\"2020-12-10\",\"repeat\":false,\"gmtCreate\":\"2020-12-16T01:34:05\",\"gmtModified\":null,\"default\":false}]},\"errmsg\":\"成功\"}";
        AssertJSONEqual(setIDToNull(res0),setIDToNull(res1),true);
        Long realID=getID(res1);
        AssertJSONEqual(setRealAdID(res2, realID),isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }

    /**
     * 测试42：在错误的segID下新建广告
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void createdAdsTest3() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(notFoundStr, isNotFound(createdAdsTestHelper(errorSegID,createNewAdStr)),true);
        AssertJSONEqual(getAdsByNullSegId,isOK(getAdsBySegIdTestHelper(nullSegID)),true);
    }

    /**
     * 测试43：创建广告：开始日期晚于结束日期
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void createdAdsTest4() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(fieldInvaildStr,isBadRequest(createdAdsTestHelper(fullSegID,createNewAdWithErrorDateStr)),true);
        AssertJSONEqual(getAdsByFullSegId,isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }

    /**
     * 测试44：创建广告：权重不合法（不是数字）
     * @return void
     * @author Zezheng Li
     * created at 12/9/20 12:45 PM
     */
    @Test
    public void createdAdsTest5() throws Exception{
        token = adminLogin("13088admin", "123456");
        AssertJSONEqual(fieldInvaildStr,isBadRequest(createdAdsTestHelper(fullSegID,createNewAdWithErrorWeightStr)),true);
        AssertJSONEqual(getAdsByFullSegId,isOK(getAdsBySegIdTestHelper(fullSegID)),true);
    }
    


    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

}
