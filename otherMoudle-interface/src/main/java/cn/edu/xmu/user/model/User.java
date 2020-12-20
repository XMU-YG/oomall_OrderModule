package cn.edu.xmu.user.model;

import java.io.Serializable;

/**
 * @author ：Zeyao Feng
 * @date ：Created in 2020/12/18 0:26
 */

public class User implements Serializable {
    private Long id;
    private String userName;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
