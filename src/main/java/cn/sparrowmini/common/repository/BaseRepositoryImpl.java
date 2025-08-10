package cn.sparrowmini.common.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public class BaseRepositoryImpl<T, ID>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager em;
    private final Class<T> domainClass;
    private final JpaEntityInformation<T, ID> entityInformation;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.domainClass = entityInformation.getJavaType();
        this.em = em;
        this.entityInformation = (JpaEntityInformation<T, ID>) entityInformation;
    }

    @Override
    public <S> Optional<S> findByIdProjection(ID id, Class<S> projectionClass) {
        String idField = idFieldName();
        Specification<T> spec = (root, query, cb) -> cb.equal(root.get(idField), id);
        return findBy(spec, q -> q.as(projectionClass).first());
    }


    @NonNull
    @Override
    public Class<T> domainType() {
        return this.domainClass;
    }

    @Override
    @NonNull
    public Class<ID> idType() {
        return entityInformation.getIdType();
    }

    @Override
    @NonNull
    public String idFieldName() {
        return entityInformation.getIdAttributeNames()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No @Id field found"));
    }

}