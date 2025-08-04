package cn.sparrowmini.common.service;

import cn.sparrowmini.common.antlr.PredicateBuilder;
import cn.sparrowmini.common.util.JpaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

import static cn.sparrowmini.common.util.JpaUtils.convertToPkValue;
import static cn.sparrowmini.common.util.JpaUtils.findPrimaryKeyField;

@Service
@RequiredArgsConstructor
public class CommonJpaServiceImpl implements CommonJpaService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Override
    public void saveEntity(String className, Object[] body) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Object o : body) {
            Object o1 = new ObjectMapper().convertValue(o, clazz);
            entityManager.persist(o1);
        }
    }

    @Transactional
    @Override
    public <T,ID> List<ID> saveEntity(String className, List<T> body) {
        List<ID> ids = new ArrayList<>();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Object o : body) {
            Object o1 = new ObjectMapper().convertValue(o, clazz);
            entityManager.persist(o1);
            ids.add((ID)JpaUtils.getPrimaryKeyValue(o1));
        }
        return ids;
    }

    @Transactional
    @Override
    public void updateEntity(String className, List<Map<String, Object>> entities) {
        entities.forEach(entity_ -> {

            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Field pkField = findPrimaryKeyField(clazz);
            String pkFieldName = pkField.getName();
            Object id = entity_.get(pkFieldName);
            Object pkValue= convertToPkValue(id,clazz);
            entity_.remove(pkFieldName);
            Object objectRef = entityManager.getReference(clazz, pkValue);
            Map<String, Object> patchMap = new HashMap<>(entity_);
            patchMap.remove(pkFieldName); // 再加一行，确保主键不会被误覆盖
            // 将 patch 字段合并进 reference 实体（只会触发一次 UPDATE）
            try {
                objectMapper
                        .readerForUpdating(objectRef)
                        .readValue(objectMapper.writeValueAsString(patchMap));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Transactional
    @Override
    public void deleteEntity(String className, Object[] ids) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for(Object id: ids){
            Object pkValue= convertToPkValue(id,clazz);
            Object entityRef = entityManager.getReference(clazz,pkValue);
            entityManager.remove(entityRef);
        }
    }

    @Transactional
    @Override
    public <T, ID> void deleteEntity(Class<T> clazz, Set<ID> ids) {
        for(ID id: ids){
            Object entityRef = entityManager.getReference(clazz,id);
            entityManager.remove(entityRef);
        }
    }

    @Transactional
    @Override
    public <T, ID> T getEntity(Class<T> clazz, ID id) {
        return entityManager.find(clazz, id);
    }

    @Transactional
    @Override
    public Object getEntity(String className, Object id) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Object pkValue= convertToPkValue(id,clazz);
        return entityManager.find(clazz, pkValue);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Object> getEntityList(String className, Pageable pageable, List<SimpleJpaFilter> filterList) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className);
        }

        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not a JPA entity: " + className);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 查询数据部分
        CriteriaQuery<Object> cq = cb.createQuery(Object.class);
        Root<?> root = cq.from(clazz);
        cq.select(root);

        // 构建动态 where 条件
        List<Predicate> predicates = this.getPredicates(root, cb, filterList);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        // 排序
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                Path<Object> path = root.get(order.getProperty());
                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
            }
            cq.orderBy(orders);
        }

        TypedQuery<Object> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Object> resultList = query.getResultList();

        // 查询总数
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Class<?> entityType = root.getModel().getBindableJavaType();
        Root<?> countRoot = countQuery.from(entityType);
        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = this.getPredicates(countRoot, cb, filterList);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(new Predicate[0]));
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Transactional
    @Override
    public <T> Page<T> getEntityList(Class<T> clazz, Pageable pageable, String filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 查询数据部分
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.select(root);

        // 构建动态 where 条件
        if ( filter!=null && !filter.isBlank()) {
            cq.where(PredicateBuilder.buildPredicate(filter, cb, root));
        }

        // 排序
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                Path<Object> path = root.get(order.getProperty());
                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
            }
            cq.orderBy(orders);
        }

        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<T> resultList = query.getResultList();

        // 查询总数
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Class<?> entityType = root.getModel().getBindableJavaType();
        Root<?> countRoot = countQuery.from(entityType);
        countQuery.select(cb.count(countRoot));
        if (filter!=null && !filter.isBlank()) {
            countQuery.where(PredicateBuilder.buildPredicate(filter, cb, countRoot));
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<Object> getEntityList(String className, Pageable pageable, String filter) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className);
        }

        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not a JPA entity: " + className);
        }
        return (Page<Object>) this.getEntityList(clazz, pageable,filter);
    }

    private List<Predicate> getPredicates(Root<?> root,CriteriaBuilder cb, List<SimpleJpaFilter> filterList){
        // 构建动态 where 条件
        List<Predicate> predicates = new ArrayList<>();
        if (filterList != null && !filterList.isEmpty()) {
            for (SimpleJpaFilter filter : filterList) {
                String field = filter.getName();
                Object value = filter.getValue();
                String op = filter.getOperator().toLowerCase();

                Path<?> path = root.get(field);

                switch (op) {
                    case "=":
                        predicates.add(cb.equal(path, value));
                        break;
                    case "like":
                        predicates.add(cb.like(path.as(String.class), "%" + value + "%"));
                        break;
                    case ">":
                        predicates.add(cb.greaterThan(path.as(Comparable.class), (Comparable) value));
                        break;
                    case "<":
                        predicates.add(cb.lessThan(path.as(Comparable.class), (Comparable) value));
                        break;
                    // 你可以继续添加更多操作符支持
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + op);
                }
            }
        }
        return predicates;
    }
}
