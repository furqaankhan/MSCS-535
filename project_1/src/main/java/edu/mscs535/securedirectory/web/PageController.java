package edu.mscs535.securedirectory.web;

import edu.mscs535.securedirectory.employee.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PageController {

    private final EmployeeRepository employeeRepository;

    public PageController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/employees")
    public String employees(@Valid @ModelAttribute("search") SearchForm search,
                            BindingResult bindingResult, Model model) {
        if (!search.getQuery().isBlank() && !bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeRepository.search(search.getQuery()));
            model.addAttribute("searched", true);
        }
        return "employees";
    }
}
