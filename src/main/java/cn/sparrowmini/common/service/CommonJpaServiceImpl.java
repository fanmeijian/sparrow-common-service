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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
    public <T, ID> List<ID> saveEntity(List<T> body) {
        List<ID> ids = new ArrayList<>();

        for (T o : body) {
            entityManager.persist(o);
            ids.add(JpaUtils.getIdValue(o));
        }
        return ids;
    }

    @Transactional
    @Override
    public <T> void updateEntity(Class<T> clazz, List<Map<String, Object>> entities) {
        entities.forEach(entity -> {
            Field pkField = JpaUtils.getIdField(clazz);
            String pkFieldName = pkField.getName();
            Object id = entity.get(pkFieldName);
            Object pkValue = JpaUtils.convertToPkValue(id, clazz);
            Object objectRef = entityManager.getReference(clazz, pkValue);
            Map<String, Object> patchMap = new HashMap<>(entity);
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
    public <T, ID> void deleteEntity(Class<T> clazz, Set<ID> ids) {
        Class<?> idClass = JpaUtils.getIdType(clazz);
        for (ID id : ids) {
            Object id_ = id;
            if (ids.toString().startsWith("{")) {
                id_ = objectMapper.convertValue(id, idClass);
            }
            Object entityRef = entityManager.getReference(clazz, id_);
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
    public <T> Page<T> getEntityList(Class<T> clazz, Pageable pageable, String filter) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        // 查询数据部分
//        CriteriaQuery<T> cq = cb.createQuery(clazz);
//        Root<T> root = cq.from(clazz);
//        cq.select(root);
//
//        // 构建动态 where 条件
//        if (filter != null && !filter.isBlank()) {
//            cq.where(PredicateBuilder.buildPredicate(filter, cb, root));
//        }

//        // 排序
//        if (pageable.getSort().isSorted()) {
//            List<Order> orders = new ArrayList<>();
//            for (Sort.Order order : pageable.getSort()) {
//                Path<Object> path = root.get(order.getProperty());
//                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
//            }
//            cq.orderBy(orders);
//        }

        TypedQuery<T> query = this.getQuery(filter, pageable, clazz);


        if (pageable.isPaged()) {
            query.setFirstResult(PageableUtils.getOffsetAsInteger(pageable));
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> {
            return this.getCountQuery(filter,clazz).getSingleResult();
        });

        // 查询总数
//        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//        Class<?> entityType = root.getModel().getBindableJavaType();
//        Root<?> countRoot = countQuery.from(entityType);
//        countQuery.select(cb.count(countRoot));
//        if (filter != null && !filter.isBlank()) {
//            countQuery.where(PredicateBuilder.buildPredicate(filter, cb, countRoot));
//        }
//        Long total = entityManager.createQuery(countQuery).getSingleResult();

//        return new PageImpl<>(resultList, pageable, total);
    }

    private <T> TypedQuery<Long> getCountQuery(String filter, Class<T> domainClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(domainClass);
        countQuery.select(cb.count(countRoot));
        if (filter != null && !filter.isBlank()) {
            countQuery.where(PredicateBuilder.buildPredicate(filter, cb, countRoot));
        }
        return this.entityManager.createQuery(countQuery);
    }

    private <T> TypedQuery<T> getQuery(String filter, Pageable pageable, Class<T> domainClass){
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        if (filter != null && !filter.isBlank()) {
            query.where(PredicateBuilder.buildPredicate(filter, builder, root));
        }

        query.select(root);
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        return entityManager.createQuery(query);
    }

}
