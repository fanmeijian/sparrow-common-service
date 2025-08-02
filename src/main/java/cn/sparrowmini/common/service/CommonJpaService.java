package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.ApiResponse;
import cn.sparrowmini.common.model.SparrowJpaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommonJpaService {

    public void saveEntity(String className, Object[] body);

    public <T,ID> List<ID> saveEntity(String className, List<T> body);

    public void updateEntity(String className, List<Map<String, Object>> entities);

    public void deleteEntity(String className, Object[] ids);

    public <T, ID> void deleteEntity(Class<T> clazz, Set<ID> ids);

    @Deprecated
    public <T> T getEntity(String className, Object id);

    public <T, ID> T getEntity(Class<T> clazz, ID id);

    @Deprecated
    public Page<Object> getEntityList(String className, Pageable pageable, List<SimpleJpaFilter> filterList);

    public <T> Page<T> getEntityList(String className, Pageable pageable, String filter);

    public <T> Page<T> getEntityList(Class<T> clazz, Pageable pageable, String filter);
}
