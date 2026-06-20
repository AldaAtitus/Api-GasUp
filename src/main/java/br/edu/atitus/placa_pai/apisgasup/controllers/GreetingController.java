package br.edu.atitus.placa_pai.apisgasup.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    @GetMapping({"","/", "/{namePath}"})
    public ResponseEntity<String> getGreeting(@RequestParam(required = false) String name,
      @PathVariable(required = false) String namePath) {
        if (name ==  null)
            name = namePath != null ? namePath : "World";
        String greeting = String.format("%s %s!", "Hello", name);
        return ResponseEntity.ok(greeting);

    }

    @PostMapping
    public ResponseEntity<String> postGreeting(
            @RequestBody String user
    ) throws Exception{
        if (user.length() > 50)
            throw new Exception("Corpo da requisição não pode ser maior que 50");
        return ResponseEntity.created(null).body(user);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> excpetionHandler(Exception ex){
        String message = ex.getMessage().replace("\r\n", "");
        return ResponseEntity.badRequest().body(message);

    }
}
