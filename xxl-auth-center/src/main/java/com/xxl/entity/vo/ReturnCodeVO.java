package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Return information after adding a new user")
public class ReturnCodeVO {

    @ApiModelProperty(notes = "return code")
    private Integer code;

    @ApiModelProperty(notes = "return message")
    private String data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReturnCodeVO{" + "code='"
                + code + '\'' + ", data='"
                + data + '\'' + '}';
    }
}
