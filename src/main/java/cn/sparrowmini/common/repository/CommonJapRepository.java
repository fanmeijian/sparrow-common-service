package cn.sparrowmini.common.repository;

import cn.sparrowmini.common.CurrentUser;
import cn.sparrowmini.common.model.CommonStateEnum;
import cn.sparrowmini.common.service.SimpleJpaFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static cn.sparrowmini.common.util.JpaUtils.convertToPkValue;
import static cn.sparrowmini.common.util.JpaUtils.findPrimaryKeyField;

@NoRepositoryBean
public interface CommonJapRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {


    /***
     * 自定义视图查询
     * @param pageable
     * @param type
     * @return
     * @param <T>
     */
    @Query("SELECT e FROM #{#entityName} e")
    <T> Page<T> findAll(Pageable pageable, Class<T> type);

    /***
     * 自定义返回的视图类
     * @param id
     * @param type
     * @return
     * @param <T>
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id=:id")
    <T> Optional<T> findById(@Param("id") ID id, Class<T> type);

    default Page<T> findAll(Pageable pageable, List<SimpleJpaFilter> filters) {
        Specification<T> specification = new Specification<T>() {

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = SimpleJpaFilterHelper.getPredicates(root, criteriaBuilder, filters);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            }
        };
        return findAll(specification, pageable);
    }
    default void update(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        Class<?> entityClass = resolveEntityClassFromRepository(this);
        Class<?> idClass = resolveIdClassFromRepository(this);

        Field pkField = findPrimaryKeyField(entityClass);
        String pkFieldName = pkField.getName();
        Object idRaw = map.get(pkFieldName);
        Object pkValue;

        if (idRaw == null) {
            throw new IllegalArgumentException("更新数据中缺少主键字段: " + pkFieldName);
        }

        if (idClass.isInstance(idRaw)) {
            pkValue = idRaw;
        } else if (idRaw instanceof String || idRaw instanceof Number) {
            pkValue = convertToPkValue(idRaw, idClass);
        } else if (idRaw instanceof Map) {
            pkValue = objectMapper.convertValue(idRaw, idClass);
        } else {
            throw new IllegalArgumentException("不支持的主键结构: " + idRaw.getClass());
        }

        Map<String, Object> patchCopy = new HashMap<>(map);
        patchCopy.remove(pkFieldName);

        T reference = getReferenceById((ID) pkValue);
        try {
            objectMapper.readerForUpdating(reference)
                    .readValue(objectMapper.writeValueAsString(patchCopy));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Patch更新失败", e);
        }

        save(reference);
    }

    default <T> Class<T> resolveEntityClassFromRepository(Object repository) {
        for (Type genericInterface : repository.getClass().getInterfaces()) {
            if (genericInterface instanceof ParameterizedType pt) {
                Type rawType = pt.getRawType();
                if (rawType instanceof Class<?> clazz &&
                        JpaRepository.class.isAssignableFrom(clazz)) {
                    Type actualType = pt.getActualTypeArguments()[0];
                    if (actualType instanceof Class<?>) {
                        return (Class<T>) actualType;
                    }
                }
            }
        }
        throw new IllegalStateException("无法解析实体类型");
    }

    default Class<?> resolveIdClassFromRepository(Object repository) {
        for (Type genericInterface : repository.getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType pt) {
                Type rawType = pt.getRawType();
                if (rawType instanceof Class<?> clazz &&
                        JpaRepository.class.isAssignableFrom(clazz)) {
                    Type actualType = pt.getActualTypeArguments()[1]; // ID
                    if (actualType instanceof Class<?>) {
                        return (Class<?>) actualType;
                    }
                }
            }
        }
        throw new IllegalStateException("无法解析主键类型");
    }


}
