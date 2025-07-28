package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.BaseTree;
import cn.sparrowmini.common.model.BaseTreeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CommonTreeServiceImpl<T extends BaseTree> implements CommonTreeService<T> {
    protected abstract BaseTreeRepository<T> getRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<?> getChildren(String parentId, Pageable pageable) {
        return getRepository().getChildren_(parentId, pageable);
    }

    @Override
    public T getNode(String id) {
        return getRepository().getReferenceById(id);
    }

    @Override
    public void moveNode(String currentId, String nextId, Object body) {
        getRepository().move(currentId, nextId);
    }

    @Override
    public ApiResponse<String> saveNode(T commonTree) {
        return new ApiResponse<>(getRepository().save(commonTree).getId());
    }

    @Override
    public void saveNode(String id, Map<String, Object> map) {
        T commonTree = getRepository().getReferenceById(id);
        // 将 patch 字段合并进 reference 实体（只会触发一次 UPDATE）
        try {
            objectMapper
                    .readerForUpdating(commonTree)
                    .readValue(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        getRepository().save(commonTree);
    }

    @Override
    public void deleteNode(Set<String> ids) {
        getRepository().deleteCascade(ids);
    }

}
