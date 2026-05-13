package lynx.team2.marketevent.exception;

import java.util.Map;

public class ErrorResponse {
    private ErrorDetail error;

    public ErrorResponse(String code, String message, Map<String,Object> details) {
        this.error= new ErrorDetail(code,message,details);
    }

    public ErrorDetail getError(){
        return error;
    }

    public static class ErrorDetail {
        private String code;
        private String message;
        private Map<String,Object> details;

        public  ErrorDetail(String code, String message, Map<String,Object> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
        public Map<String,Object> getDetails() { return details; }
    }
}
