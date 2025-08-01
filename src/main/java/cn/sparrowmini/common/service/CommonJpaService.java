package cn.sparrowmini.common.service;

import cn.sparrowmini.common.model.SparrowJpaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommonJpaService {

    public void saveEntity(String className, Object[] body);

    public void updateEntity(String className, List<Map<String, Object>> entities);

    public void deleteEntity(String className, Object[] ids);

    public Object getEntity(String className, Object id);

    public Page<Object> getEntityList(String className, Pageable pageable, List<SimpleJpaFilter> filterList);

    public Page<Object> getEntityList(String className, Pageable pageable, String filter);
}
