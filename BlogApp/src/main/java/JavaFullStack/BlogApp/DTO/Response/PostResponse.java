package JavaFullStack.BlogApp.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String status;
    private String authorUsername;
    private Set<String> tags;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
