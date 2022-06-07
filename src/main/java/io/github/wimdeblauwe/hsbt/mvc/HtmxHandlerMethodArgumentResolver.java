package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static io.github.wimdeblauwe.hsbt.mvc.HtmxRequestHeader.*;

public class HtmxHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(HtmxRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return createHtmxRequest(Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)));
    }

    public static HtmxRequest createHtmxRequest(HttpServletRequest req) {
        String hxRequestHeader = req.getHeader(HX_REQUEST.getValue());
        if (hxRequestHeader == null) {
            return new HtmxRequest.Builder(false).build();
        }

        HtmxRequest.Builder builder = new HtmxRequest.Builder(true);
        if (req.getHeader(HX_BOOSTED.getValue()) != null) {
            builder.withBoosted(true);
        }
        if (req.getHeader(HX_CURRENT_URL.getValue()) != null) {
            builder.withCurrentUrl(req.getHeader(HX_CURRENT_URL.getValue()));
        }
        if (req.getHeader(HX_HISTORY_RESTORE_REQUEST.getValue()) != null) {
            builder.withHistoryRestoreRequest(true);
        }
        if (req.getHeader(HX_PROMPT.getValue()) != null) {
            builder.withPromptResponse(req.getHeader(HX_PROMPT.getValue()));
        }
        if (req.getHeader(HX_TARGET.getValue()) != null) {
            builder.withTarget(req.getHeader(HX_TARGET.getValue()));
        }
        if (req.getHeader(HX_TRIGGER_NAME.getValue()) != null) {
            builder.withTriggerName(req.getHeader(HX_TRIGGER_NAME.getValue()));
        }
        if (req.getHeader(HX_TRIGGER.getValue()) != null) {
            builder.withTriggerId(req.getHeader(HX_TRIGGER.getValue()));
        }
        return builder.build();
    }
}
