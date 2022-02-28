package easyoa.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by claire on 2019-06-26 - 13:35
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode<T> {
    private String id;

    private String key;

    private String icon;

    private String title;

    private String value;

    private String text;

    private String permissions;

    private String type;

    private Double orderNum;

    private String path;

    private String component;

    private List<TreeNode<T>> children;

    private String parentId;

    private boolean hasParent = false;

    private boolean hasChildren = false;

    private String createTime;

    private String updateTime;

    private String assigneeNames;

    private boolean status;

    private String groupName;

    private T entity;

    public void initChildren(){
        this.children = new ArrayList<>();
    }
}
