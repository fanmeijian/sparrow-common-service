package cn.sparrowmini.common.model;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoRepositoryBean
public interface BaseTreeRepository<S extends BaseTree> extends BaseEntityJpaRepository<S, String> {

    @Query("select max(t.seq) from #{#entityName} t where t.parentId is null")
    BigDecimal getRootMaxSeq();

    @Query("select max(t.seq) from #{#entityName} t where t.parentId=:parentId")
    BigDecimal getMaxSeqByParentId(String parentId);

    @Query("select max(t.seq) from #{#entityName} t where t.parentId=:parentId and t.seq<:seq")
    BigDecimal getPreSeqByParentId(String parentId, BigDecimal seq);

    @Query("select max(t.seq) from #{#entityName} t where t.parentId is null and t.seq<:seq")
    BigDecimal getRootPreSeq(BigDecimal seq);

    @Query("select min(t.seq) from #{#entityName} t where t.parentId is null")
    BigDecimal getRootFirstSeq();

    @Query("select min(t.seq) from #{#entityName} t where t.parentId=:parentId")
    BigDecimal getFirstSeqByParentId(String parentId);

    Page<S> findByParentId(String parentId, Pageable pageable);

    long countByParentId(String parentId);

    @Query("select new cn.sparrowmini.common.model.BaseTree(p,count(c.parentId)) from #{#entityName} p LEFT JOIN #{#entityName} c " +
            "on c.parentId = p.id " +
            "where p.parentId=:parentId or (:parentId is null and p.parentId is null) " +
            "group by c.parentId,p " +
            "order by p.seq,p.createdDate")
    Page<BaseTree> getChildren(String parentId, Pageable pageable);

    default Page<S> getChildren_(String parentId, Pageable pageable){
        Page<S> children= this.findByParentId(parentId, pageable);
        children.forEach(child->{
            long count  = countByParentId(child.getId());
            child.setChildCount(count);
        });
        return children;
    }

    /***
     * 移动当前节点重新排序
     * @param currentId
     * @param nextId
     */
    @Transactional
    default void move(String currentId, String nextId) {
        BigDecimal step = BigDecimal.valueOf(0.0001);
        BigDecimal two = BigDecimal.valueOf(2);
        S current = this.getReferenceById(currentId);
        S next = nextId == null ? null : this.getReferenceById(nextId);
        BigDecimal newSeq = null;

        if(next!=null){
            final BigDecimal nextSeq=next.getSeq();
            if(current.getParentId()!=null && next.getParentId()!=null && current.getParentId().equals(next.getParentId())){
               BigDecimal preSeq= getPreSeqByParentId(current.getParentId(), next.getSeq());
               if(preSeq==null){
                   // move to first node
                   newSeq =  nextSeq.subtract(step);
               }else{
                   // insert to middle
                   newSeq =  preSeq.add(nextSeq).divide(two);
               }


            }else if(current.getParentId()==null && next.getParentId()==null){
                BigDecimal preSeq=getRootPreSeq(nextSeq);
                if(preSeq==null){
                    // move to root first node
                    newSeq =  nextSeq.subtract(step);
                }else{
                    // insert to middle
                    newSeq =  preSeq.add(nextSeq).divide(two);
                }


            }else{
                throw new RuntimeException(String.format("不能插入到不同层级的前后节点 前 %s 后 %s", "无", next.getName() + nextId));
            }
        }

        if(next==null){
            // move the last
            if(current.getParentId()==null){
                newSeq = getRootMaxSeq().add(step);
            }else{
                newSeq = getMaxSeqByParentId(current.getParentId()).add(step);
            }

        }
        current.setSeq(newSeq);
        this.save(current);
    }

    void deleteByParentId(String parentId);

    @Transactional
    default void deleteCascade(Collection<String> ids) {
        deleteAllById(ids);
        ids.forEach(id -> {
            if (countByParentId(id) > 0) {
                deleteCascade(getChildren(id, Pageable.unpaged()).getContent().stream().filter(f -> f.getChildCount() > 0).map(BaseUuidEntity::getId).collect(Collectors.toList()));
                deleteByParentId(id);
            }
        });

    }

    default Page<S> getAllChildren(String parentId, Pageable pageable) {
        Page<S> rootPage = findByParentId(parentId, pageable);
        List<S> root = rootPage.getContent();
        root.forEach(r -> {
            if (countByParentId(r.getId()) > 0) {
                List<?> children = getAllChildren(r.getId(), pageable).getContent();
                r.getChildren().addAll(children);
                r.setChildCount(children.size());

            }
        });
        return new PageImpl<>(root, pageable, rootPage.getTotalElements());
    }

    S getReferenceByCode(String code);

    boolean existsByCode(String code);
}
