package JavaFullStack.BlogApp.Repository;

import JavaFullStack.BlogApp.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}