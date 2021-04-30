package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Searched out users' information")
public class SearchVO {

    @ApiModelProperty(notes = "total count")
    private Long total;

    @ApiModelProperty(notes = "UserDetailForDisplayVO list")
    private List<UserDetailForDisplayVO> rows;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<UserDetailForDisplayVO> getRows() {
        return rows;
    }

    public void setRows(List<UserDetailForDisplayVO> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "SearchVO{" + "total="
                + total + ", rows="
                + rows + '}';
    }
}
