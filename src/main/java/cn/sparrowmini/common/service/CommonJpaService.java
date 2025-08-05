package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.SparrowJpaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommonJpaService {

    public <T, ID> List<ID> saveEntity(List<T> body);

    public <T> void updateEntity(Class<T> clazz, List<Map<String, Object>> entities);

    public <T, ID> void deleteEntity(Class<T> clazz, Set<ID> ids);

    public <T, ID> T getEntity(Class<T> clazz, ID id);

    public <T> Page<T> getEntityList(Class<T> clazz, Pageable pageable, String filter);

}
