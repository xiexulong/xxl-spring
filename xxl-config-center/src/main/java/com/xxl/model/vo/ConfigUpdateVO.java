
package com.xxl.model.vo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUpdateVO {

    @NotEmpty
    @Valid
    private List<Pair> configs;

    public List<Pair> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Pair> configs) {
        this.configs = configs;
    }

    public Map<Long, String> getConfigMap() {
        return configs.stream().collect(Collectors.toMap(Pair::getId, Pair::getValue));
    }

    public static class Pair {

        @NotNull
        private Long id;

        @NotNull
        private String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
