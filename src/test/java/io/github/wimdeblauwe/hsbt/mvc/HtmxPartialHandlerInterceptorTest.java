package io.github.wimdeblauwe.hsbt.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.test.web.servlet.result.XpathResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.xpath.XPathExpressionException;

import static io.github.wimdeblauwe.hsbt.mvc.support.PartialXpathResultMatchers.partialXpath;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartialsController.class)
class HtmxPartialHandlerInterceptorTest {

    @MockBean
    private PartialsController.TodoRepository todoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testASinglePartialCanBeReturned() throws Exception {
        mockMvc.perform(get("/partials/first"))
                .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(xpath("/ul").exists())
               .andExpect(xpath("/ul[@hx-swap-oob='true']").doesNotExist());
    }

    @Test
    public void testAMainChange() throws Exception {
        mockMvc.perform(get("/partials/main-and-partial"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
                //Note xpath can't be used on partials because the document doesn't have a single root
               .andExpect(partialXpath("//*[@id='userCount'][@hx-swap-oob]").exists())
               .andExpect(partialXpath("/ul").exists())
               .andExpect(partialXpath("/ul[@hx-swap-oob]").doesNotExist());
    }

    @Test
    public void testHeaders() throws Exception {
        mockMvc.perform(get("/partials/triggers"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "usersCounted"))
               .andExpect(header().string("HX-Trigger-After-Settle", "{\"usersCountedSettle1\":\"aDetail\",\"usersCountedSettle2\":null}"))
               .andExpect(header().string("HX-Trigger-After-Swap", "{\"usersCountedSwap\":\"swap detail\"}"))
               .andExpect(header().string("HX-Push", "/a/newHistory"))
               .andExpect(header().string("HX-Redirect", "/a/redirect"))
               .andExpect(header().string("HX-Refresh", "true"))
               .andExpect(header().string("HX-Retarget", "#newTarget"));
    }
    @Test
    public void testExtension() throws Exception {
        mockMvc.perform(get("/partials/extension"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(partialXpath("//*[@id='alert'][@hx-swap-oob]").exists())
               .andExpect(header().string("HX-Trigger", "alertSent"));
    }

    @Test
    public void testPostTodo() throws Exception {
        when(todoRepository.getNumberOfActiveItems()).thenReturn(0);

        mockMvc.perform(post("/partials/add-todo")
                                .param("name", "A Todo Name")
                                )
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
                //Note xpath can't be used on partials because the document doesn't have a single root
               .andExpect(partialXpath("//*[@id='active-items-count'][@hx-swap-oob]").exists())
               .andExpect(partialXpath("/span[@id='item']").exists())
               .andExpect(partialXpath("/span[@id='item'][@hx-swap-oob]").doesNotExist());
    }

    @Test
    public void testHtmxRequestExpressionUtility() throws Exception {
        mockMvc.perform(get("/partials/expressionUtility").header("HX-Request", "true"))
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmxRequest']").exists())
               .andExpect(xpath("//div[@id='webRequest']").exists());
    }

    @Test
    public void testHtmxRequestExpressionUtilityWorkEvenWhenNotHtmxRequest() throws Exception {
        mockMvc.perform(get("/partials/expressionUtility")) //No HX-Request header
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmxRequest']").doesNotExist());
    }



}
