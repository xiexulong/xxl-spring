package com.xxl.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(description = "algorithm config vo")
public class AlgorithmConfigVO {

    @ApiModelProperty(notes = "the algorithm config file name")
    @NotBlank(message = "fileName is required.")
    private String fileName;

    @ApiModelProperty(notes = "the algorithm config file content")
    @NotBlank(message = "content is required.")
    private String content;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
