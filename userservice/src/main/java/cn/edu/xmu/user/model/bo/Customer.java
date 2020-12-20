package cn.edu.xmu.user.model.bo;

import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.user.model.po.UserPo;
import cn.edu.xmu.user.model.vo.CustomerChangeVo;
import cn.edu.xmu.user.model.vo.CustomerVo;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Customer {
    public static String AESPASS = "OOAD2020-12-02";
    private State state = State.NEW;
    public enum State {
        NEW(0, "后台用户"),
        NORM(4, "正常用户"),
        FORBID(6, "被封禁用户");

        public static final State DELETE = null;
        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Customer.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Customer.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private String userName;

    private String password;

    private String realname;

    private Byte gender;

    private LocalDate birthday;

    private Long point;

    private String mobile;

    private String email;

    private Long bedeleted;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
    public Customer(UserPo po){
        this.id = po.getId();
        this.userName = po.getUserName();
        this.password =po.getPassword();
        this.realname = po.getRealName();
        this.gender=0;
        this.birthday=po.getBirthday();
        this.point=0L;
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }
        this.mobile = po.getMobile();
        this.email=po.getEmail();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public UserPo createUpdatePo(CustomerVo vo) {
        String nameEnc = vo.getRealname() == null ? null : AES.encrypt(vo.getRealname(), Customer.AESPASS);
        String mobEnc = vo.getMobile() == null ? null : AES.encrypt(vo.getMobile(), Customer.AESPASS);
        String emlEnc = vo.getEmail() == null ? null : AES.encrypt(vo.getEmail(), Customer.AESPASS);
        Byte state = (byte) this.state.code;

        UserPo po = new UserPo();
        po.setId(id);
        po.setRealName(nameEnc);
        po.setMobile(mobEnc);
        po.setEmail(emlEnc);
        po.setState(state);
        po.setGmtModified(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    public UserPo createUpdatePo(CustomerChangeVo vo) {
        Byte state = (byte) this.state.code;

        UserPo po = new UserPo();
        po.setId(id);
        po.setRealName(vo.getRealname());
        po.setGender((byte) 0);
        po.setState(state);
        po.setGmtModified(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

}
