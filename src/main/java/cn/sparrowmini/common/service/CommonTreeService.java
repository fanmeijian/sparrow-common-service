package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.BaseTree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Set;

public interface CommonTreeService<T extends BaseTree> {

    /**
     * 获取子节点
     * @param parentId
     * @param pageable
     * @return
     */
    public Page<?> getChildren(String parentId, Pageable pageable);

    /**
     * 节点详情
     * @param id
     * @return
     */
    public T getNode(String id);

    /**
     * 移动节点
     * @param currentId
     * @param nextId
     * @param body
     */
    public void moveNode(String currentId, String nextId, @Nullable Object body);

    /**
     * 新增节点
     * @param commonTree
     * @return
     */
    public ApiResponse<String> saveNode(T commonTree);

    /***
     * 更新节点
     * @param id
     * @param map
     */
    public void saveNode(String id ,Map<String, Object> map);

    /**
     * 删除节点
     * @param ids
     */
    public void deleteNode(Set<String> ids);
}
