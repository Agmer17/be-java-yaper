package app.model.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private String status;
    private String message;
    private Object data;
}
