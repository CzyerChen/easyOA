package easyoa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by claire on 2019-06-26 - 09:11
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VueRouter<T> implements Serializable {
    private static final long serialVersionUID = -3327478146308500708L;
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String parentId;

    private String path;
    private String name;
    private String component;
    private String icon;
    private String redirect;
    private RouterMeta meta;
    private List<VueRouter<T>> children;

    @JsonIgnore
    private Boolean hasParent = false;
    @JsonIgnore
    private Boolean hasChildren = false;

    public void initChildren(){
        this.children = new ArrayList<>();
    }
}
