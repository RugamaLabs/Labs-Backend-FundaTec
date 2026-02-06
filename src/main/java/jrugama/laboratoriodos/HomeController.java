package jrugama.laboratoriodos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/inicio")
    public String start(){
        return "start";
    }
    @GetMapping("/final")
    public String end(){
        return "end";
    }


}
