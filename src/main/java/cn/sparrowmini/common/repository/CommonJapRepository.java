package cn.sparrowmini.common.repository;

import cn.sparrowmini.common.CurrentUser;
import cn.sparrowmini.common.antlr.PredicateBuilder;
import cn.sparrowmini.common.model.CommonStateEnum;
import cn.sparrowmini.common.service.SimpleJpaFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    default Page<T> findAll(Pageable pageable, String filter) {
        Specification<T> specification = new Specification<T>() {

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return PredicateBuilder.buildPredicate(filter, criteriaBuilder, root);
            }
        };
        return findAll(specification, pageable);
    }

    default <U> Page<U> findAll(Pageable pageable, String filter, Class<U> projectionClass) {
        Specification<T> specification = new Specification<T>() {

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return PredicateBuilder.buildPredicate(filter, criteriaBuilder, root);
            }
        };
        return findBy(
                specification,
                query -> query.as(projectionClass).page(pageable)
        );
    }

    default void updateAll(List<Map<String, Object>> mapList, Class<?> entityClass, Class<?> idClass) {
        List<T> entities = new ArrayList<>();
        mapList.forEach(map -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 推荐，避免变成 timestamp
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

            if (existsById((ID) pkValue)) {
                Map<String, Object> patchCopy = new HashMap<>(map);
                patchCopy.remove(pkFieldName);

                T reference = getReferenceById((ID) pkValue);
                try {
                    objectMapper.readerForUpdating(reference)
                            .readValue(objectMapper.writeValueAsString(patchCopy));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Patch更新失败" + e.getOriginalMessage(), e);
                }
                entities.add(reference);
            } else {
                T newEntity = (T) objectMapper.convertValue(map, entityClass);
                entities.add(newEntity);
            }

        });
        saveAll(entities);
    }

    default void update(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 推荐，避免变成 timestamp
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
        Class<?> clazz = repository.getClass();
        while (clazz != null) {
            for (Type genericInterface : clazz.getGenericInterfaces()) {
                if (genericInterface instanceof ParameterizedType pt) {
                    Type rawType = pt.getRawType();
                    if (rawType instanceof Class<?> rawClazz &&
                            JpaRepository.class.isAssignableFrom(rawClazz)) {
                        Type actualType = pt.getActualTypeArguments()[0];
                        if (actualType instanceof Class<?>) {
                            return (Class<T>) actualType;
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass(); // 向上查找父类
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

    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        return (Class<T>) RepositoryUtils.getDomainType(this);
    }
}
