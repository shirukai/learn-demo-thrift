package lear.demo.thrift.client.dto;

import java.util.List;

/**
 * Created by shirukai on 2019-06-27 10:06
 * demoDTO
 */
public class DemoInfoDTO {
    private Integer id;
    private String name;
    private List<String> tags;

    public Integer getId() {
        return id;
    }

    public DemoInfoDTO setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DemoInfoDTO setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public DemoInfoDTO setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }
}
