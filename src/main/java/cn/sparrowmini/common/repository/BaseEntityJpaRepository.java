package cn.sparrowmini.common.repository;

import cn.sparrowmini.common.CurrentUser;
import cn.sparrowmini.common.model.BaseEntity;
import cn.sparrowmini.common.model.CommonStateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface BaseEntityJpaRepository<S extends BaseEntity, ID> extends JpaRepository<S, ID>, JpaSpecificationExecutor<S> {
    Page<S> findByCreatedBy(String username, Pageable pageable);

    Page<S> findByModifiedBy(String username, Pageable pageable);

    @Query("SELECT e FROM #{#entityName} e WHERE e.createdDate BETWEEN :startDate AND :endDate")
    Page<S> findCreatedDateBetween(Date startDate, Date endDate, Pageable pageable);

    default Page<S> findMyAll( Pageable pageable) {
        return findByCreatedBy(CurrentUser.get(), pageable);
    }

    @Query("SELECT e FROM #{#entityName} e WHERE e.modifiedDate BETWEEN :startDate AND :endDate")
    Page<S> findModifiedDateBetween(Date startDate, Date endDate, Pageable pageable);

    Page<S> findByStat(String stat, Pageable pageable);

    Page<S> findByEntityStat(String entityStat, Pageable pageable);

    Page<S> findByStatIn(Set<String> stat, Pageable pageable);

    Page<S> findByEntityStatIn(Set<String> entityStat, Pageable pageable);

    default void updateStat(ID id,String stat){
        S s = getReferenceById(id);
        s.setStat(stat);
        save(s);
    }

    default void updateEntityStat(ID id, CommonStateEnum entityState){
        S s = getReferenceById(id);
        s.setEntityStat(entityState);
        save(s);
    }

    default void enable(ID id){
        S s = getReferenceById(id);
        s.setEnabled(true);
        save(s);
    }

    default void disable(ID id,String stat){
        S s = getReferenceById(id);
        s.setEnabled(false);
        save(s);
    }

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
}
