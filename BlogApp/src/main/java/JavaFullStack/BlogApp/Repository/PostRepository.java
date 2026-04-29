package JavaFullStack.BlogApp.Repository;

import JavaFullStack.BlogApp.Entity.Post;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Enums.PostStatus;
import org.springframework.data.domain.Page;       // CORRECT
import org.springframework.data.domain.Pageable;   // CORRECT
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    Page<Post> findByAuthor(User author, Pageable pageable);
    Page<Post> findByTagsNameAndStatus(String tagName, PostStatus status, Pageable pageable);

    boolean existsBySlug(String slug);
}