package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.BaseTree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Set;

public interface CommonTreeServiceV2 {

    /**
     * 获取子节点
     * @param parentId
     * @param pageable
     * @return
     */
    public Page<?> getChildren(String parentId, Pageable pageable, Class<? extends BaseTree> clazz);

    /**
     * 节点详情
     * @param id
     * @return
     */
    public Object getNode(String id,Class<? extends BaseTree> entityClass);

    /**
     * 移动节点
     * @param currentId
     * @param nextId
     * @param body
     */
    public void moveNode(String currentId, String nextId, @Nullable Object body,Class<? extends BaseTree> entityClass);

    /**
     * 新增节点
     * @param commonTree
     * @return
     */
    public ApiResponse<String> saveNode(BaseTree commonTree,Class<? extends BaseTree> entityClass);

    /***
     * 更新节点
     * @param id
     * @param map
     */
    public void saveNode(String id ,Map<String, Object> map,Class<? extends BaseTree> entityClass);

    /**
     * 删除节点
     * @param ids
     */
    public void deleteNode(Set<String> ids,Class<? extends BaseTree> entityClass);
}
