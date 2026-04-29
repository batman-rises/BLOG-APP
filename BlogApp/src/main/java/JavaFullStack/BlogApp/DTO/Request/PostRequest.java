package JavaFullStack.BlogApp.DTO.Request;

import lombok.Data;
import java.util.Set;

@Data
public class PostRequest {
    private String title;
    private String content;
    private String status;       // "DRAFT" or "PUBLISHED"
    private Set<String> tags;    // ["java", "spring"]
}