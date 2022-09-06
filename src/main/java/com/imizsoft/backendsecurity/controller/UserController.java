package com.imizsoft.backendsecurity.controller;


import com.imizsoft.backendsecurity.payload.JwtResponse;
import com.imizsoft.backendsecurity.payload.MessageResponse;
import com.imizsoft.backendsecurity.payload.SigninRequest;
import com.imizsoft.backendsecurity.payload.SignupRequest;
import com.imizsoft.backendsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody SigninRequest request){
        JwtResponse jwtResponse = userService.authenticateUser(request);
        return new ResponseEntity<JwtResponse>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequest request){
        MessageResponse messageResponse = userService.registerUser(request);
        if(messageResponse.getCode() == 0)
            return ResponseEntity.badRequest().body(messageResponse);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token){
        MessageResponse messageResponse = userService.confirmToken(token);
        String details = "";
        if(messageResponse.getCode() == 1)
            details = "You can now connect with your account and use the application";
        String html = "<html>\n" +
                "\t<header>\n" +
                "\t\t<title>Welcome</title>\n" +
                "\t</header>\n" +
                "\t<body>\n" +
                "\t\t<h2>"+messageResponse.getMessage()+"</h2>\n" +
                "\t\t<h4>"+details+"</h4>\n" +
                "\t</body>\n" +
                "</html>";

        return html;
    }

}
