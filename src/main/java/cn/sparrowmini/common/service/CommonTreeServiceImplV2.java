package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.BaseTree;
import cn.sparrowmini.common.model.BaseTreeRepository;
import cn.sparrowmini.common.repository.TreeRepositoryFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CommonTreeServiceImplV2 implements CommonTreeServiceV2 {
    @Autowired
    private TreeRepositoryFactory treeRepositoryFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<?> getChildren(String parentId, Pageable pageable,Class<? extends BaseTree> entityClass) {
        return treeRepositoryFactory.getRepository(entityClass).getChildren_(parentId, pageable);
    }

    @Override
    public Object getNode(String id,Class<? extends BaseTree> entityClass) {
        return treeRepositoryFactory.getRepository(entityClass).getReferenceById(id);
    }

    @Override
    public void moveNode(String currentId, String nextId, Object body,Class<? extends BaseTree> entityClass) {
        treeRepositoryFactory.getRepository(entityClass).move(currentId, nextId);
    }

    @Override
    public ApiResponse<String> saveNode(BaseTree commonTree,Class<? extends BaseTree> entityClass) {
        return new ApiResponse<>(treeRepositoryFactory.getRepository(entityClass).save(commonTree).getId());
    }

    @Override
    public void saveNode(String id, Map<String, Object> map,Class<? extends BaseTree> entityClass) {
        BaseTree commonTree = treeRepositoryFactory.getRepository(entityClass).getReferenceById(id);
        // 将 patch 字段合并进 reference 实体（只会触发一次 UPDATE）
        try {
            objectMapper
                    .readerForUpdating(commonTree)
                    .readValue(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        treeRepositoryFactory.getRepository(entityClass).save(commonTree);
    }

    @Override
    public void deleteNode(Set<String> ids,Class<? extends BaseTree> entityClass) {
        treeRepositoryFactory.getRepository(entityClass).deleteCascade(ids);
    }

}
