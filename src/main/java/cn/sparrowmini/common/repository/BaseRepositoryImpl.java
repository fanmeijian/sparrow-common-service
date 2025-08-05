package cn.sparrowmini.common.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class BaseRepositoryImpl<T, ID>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager em;
    private final Class<T> domainClass;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.domainClass = entityInformation.getJavaType();
        this.em = em;
    }

    @Override
    public <S> Optional<S> findByIdProjection(ID id, Class<S> projectionClass) {
        String idField = findIdFieldName(domainClass);

        Specification<T> spec = (root, query, cb) -> cb.equal(root.get(idField), id);

        return findBy(spec, q -> q.as(projectionClass).first());
    }

    private String findIdFieldName(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getName();
            }
        }
        throw new IllegalStateException("No @Id field found in entity: " + clazz.getName());
    }


}