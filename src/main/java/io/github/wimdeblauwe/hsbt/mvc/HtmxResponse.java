package io.github.wimdeblauwe.hsbt.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Representation of HTMX partials.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 */
final public class HtmxResponse {
    private static final Logger LOG = LoggerFactory.getLogger(HtmxResponse.class);

	private final Set<String> templates;
	private final Map<String, String> triggers;
	private final Map<String, String> triggersAfterSettle;
	private final Map<String, String> triggersAfterSwap;
    private String headerRetarget;
    private boolean headerRefresh;
    private String headerRedirect;
    private String headerPushHistory;

    public HtmxResponse() {
        this.templates = new HashSet<>();
        this.triggers = new HashMap<>();
        this.triggersAfterSettle = new HashMap<>();
        this.triggersAfterSwap = new HashMap<>();
	}
	
	/**
	 * Append the rendered template or fragment.
	 *
	 * @param template must not be {@literal null} or empty.
	 * @return same HtmxResponse for chaining
	 */
	public HtmxResponse addTemplate(String template) {
        Assert.hasText(template, "template should not be blank");
        templates.add(template);
        return this;
	}

    /**
     * Set a HX-Trigger header. Multiple trigger were automatically be merged into the same header.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     *
     * @param eventName must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     */
    public HtmxResponse addTrigger(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        return addTrigger(eventName, null, HxTriggerLifecycle.RECEIVE);
    }

    /**
     * Set a HX-Trigger (or HX-Trigger-After-Settle or HX-Trigger-After-Swap headers.
     * Multiple trigger were
     * automatically be merged into the same header.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     *
     * @param eventName must not be {@literal null} or empty.
     * @param eventDetail can be {@literal null}.
     * @param step must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     */
    public HtmxResponse addTrigger(String eventName, String eventDetail, HxTriggerLifecycle step) {
        Assert.hasText(eventName, "eventName should not be blank");
        switch(step) {
            case RECEIVE:
                triggers.put(eventName, eventDetail);
                break;
            case SETTLE:
                triggersAfterSettle.put(eventName, eventDetail);
                break;
            case SWAP:
                triggersAfterSwap.put(eventName, eventDetail);
                break;
            default:
                throw new IllegalArgumentException("Unknown step "+ step);
        }
        return this;
    }

    /**
     * Pushes a new url into the history stack
     *
     * @param url must not be {@literal null} or empty. {@literal false} prevents the browser history from being updated
     * @return same HtmxResponse for chaining
     *
     * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
     */
    public HtmxResponse pushHistory(String url) {
        Assert.hasText(url, "url should not be blank");
        this.headerPushHistory = url;
        return this;
    }

    /**
     * Can be used to do a client-side redirect to a new location
     *
     * @param url can be a relative or an absolute url
     * @return same HtmxResponse for chaining
     */
    public HtmxResponse browserRedirect(String url) {
        Assert.hasText(url, "url should not be blank");
        this.headerRedirect = url;
        return this;
    }

    /**
     * If set to "true" the client side will do a a full refresh of the page
     *
     * @param refresh boolean to indicate full refresh or not.
     * @return same HtmxResponse for chaining
     */
    public HtmxResponse browserRefresh(boolean refresh) {
        this.headerRefresh = refresh;
        return this;
    }

    /**
     * Set a CSS selector that updates the target of the content update to a different element on the page
     *
     * @param cssSelector must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     */
    public HtmxResponse retarget(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.headerRetarget = cssSelector;
        return this;
    }

    /**
     *
     * @param otherResponse Another HtmxResponse that will be merged into this response.
     * @return this for chaining
     */
    public HtmxResponse and(HtmxResponse otherResponse){
        otherResponse.templates.forEach(otherTemplate -> {
            if(!this.templates.add(otherTemplate)) {
                LOG.info("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
            }
        });
        mergeMapAndLog(HxTriggerLifecycle.RECEIVE, this.triggers, otherResponse.triggers);
        mergeMapAndLog(HxTriggerLifecycle.SETTLE, this.triggersAfterSettle, otherResponse.triggersAfterSettle);
        mergeMapAndLog(HxTriggerLifecycle.SWAP, this.triggersAfterSwap, otherResponse.triggersAfterSwap);

        if(otherResponse.getHeaderPushHistory() != null) {
            this.headerPushHistory = otherResponse.getHeaderPushHistory();
        }
        if(otherResponse.getHeaderRedirect() != null) {
            this.headerRedirect = otherResponse.getHeaderRedirect();
        }
        if(otherResponse.getHeaderRefresh()) {
            this.headerRefresh = true;
        }
        if(otherResponse.getHeaderRedirect() != null) {
            this.headerRedirect = otherResponse.getHeaderRedirect();
        }

        return this;
    }

    private void mergeMapAndLog(HxTriggerLifecycle receive, Map<String, String> triggers, Map<String, String> otherTriggers) {
        otherTriggers.forEach((key, value) -> {
            if(LOG.isInfoEnabled()) {
                if(triggers.containsKey(key)) {
                    String matchingTrigger = triggers.get(key);
                    LOG.info("Duplicate {} entry: event '{}' details '{}' will be overwritten by with '{}'", receive.getHeaderName(), key, matchingTrigger, value);
                }
            }
            triggers.put(key, value);
        });
    }


	Set<String> getTemplates() {
		return new HashSet<>(templates);
	}

    Map<String, String> getTriggers() {
        if(this.triggers.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(this.triggers);
    }

    Map<String, String> getTriggersAfterSettle() {
        if(this.triggers.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(this.triggersAfterSettle);
    }

    Map<String, String> getTriggersAfterSwap() {
        if(this.triggers.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(this.triggersAfterSwap);
    }

    String getHeaderRetarget() {
        return headerRetarget;
    }

    boolean getHeaderRefresh() {
        return headerRefresh;
    }

    String getHeaderRedirect() {
        return headerRedirect;
    }

     String getHeaderPushHistory() {
        return headerPushHistory;
    }
}
