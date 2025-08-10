package cn.sparrowmini.common.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

     <S> Optional<S> findByIdProjection(ID id, Class<S> projectionClass);

     @NonNull
     Class<T> domainType();

     @NonNull
     Class<ID> idType();

     @NonNull
     String idFieldName();
}
