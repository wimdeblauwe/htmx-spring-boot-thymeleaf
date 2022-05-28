package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("forms")
public class FormsController {

    public static class FormValues {
        private String email;
        private String firstName;
        private String lastName;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    @GetMapping
    public String getForm(@ModelAttribute("body") FormValues body, BindingResult result) {
        return "forms";
    }

    @PostMapping("newAccount")
    public String submitForm(HttpServletRequest request, HtmxRequest htmxReq,
                             @ModelAttribute("body") FormValues body, BindingResult result, Model model) {
        if(body.email != null && body.email.contains("gmail")) {
            result.rejectValue("email",  "no-gmail", "I don't like gmail");
        }

        if(htmxReq.getTriggerName() != null && htmxReq.getTriggerName().equals("email")) {
            model.addAttribute("alertText", "Email Alert: "+new Random().nextInt());

            model.addAttribute("baseFragmentName", "forms :: emailField");
            model.addAttribute("fragmentName", "forms :: alert");
            return "multi-fragment";
//            return "forms :: emailField"; // This works
        }

        return "forms :: formBody";
    }

}
