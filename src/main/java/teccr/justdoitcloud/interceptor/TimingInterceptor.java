package teccr.justdoitcloud.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TimingInterceptor implements HandlerInterceptor {

    private static final String ATTR_START_TIME = "handlerStartTimeNanos";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long start = System.nanoTime();
        request.setAttribute(ATTR_START_TIME, start);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        Object attr = request.getAttribute(ATTR_START_TIME);
        if (attr instanceof Long) {
            long start = (Long) attr;
            long end = System.nanoTime();
            double seconds = (end - start) / 1_000_000_000.0;

            // log.info("Handler {} executed in {} seconds", handler.toString(),
            // String.format("%.4f", seconds));
        }
    }
}
