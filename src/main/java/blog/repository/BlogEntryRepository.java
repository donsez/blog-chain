package blog.repository;

import blog.domain.BlogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the BlogEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlogEntryRepository extends JpaRepository<BlogEntry, Long> {

    @Query(value = "select distinct blog_entry from BlogEntry blog_entry left join fetch blog_entry.tags",
        countQuery = "select count(distinct blog_entry) from BlogEntry blog_entry")
    Page<BlogEntry> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct blog_entry from BlogEntry blog_entry left join fetch blog_entry.tags")
    List<BlogEntry> findAllWithEagerRelationships();

    @Query("select blog_entry from BlogEntry blog_entry left join fetch blog_entry.tags where blog_entry.id =:id")
    Optional<BlogEntry> findOneWithEagerRelationships(@Param("id") Long id);

}
