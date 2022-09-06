package com.imizsoft.backendsecurity.service;

import com.imizsoft.backendsecurity.model.ConfirmationToken;
import com.imizsoft.backendsecurity.model.ERole;
import com.imizsoft.backendsecurity.model.Role;
import com.imizsoft.backendsecurity.model.User;
import com.imizsoft.backendsecurity.payload.JwtResponse;
import com.imizsoft.backendsecurity.payload.MessageResponse;
import com.imizsoft.backendsecurity.payload.SigninRequest;
import com.imizsoft.backendsecurity.payload.SignupRequest;
import com.imizsoft.backendsecurity.reprository.RoleRepository;
import com.imizsoft.backendsecurity.reprository.UserRepository;
import com.imizsoft.backendsecurity.security.jwt.JwtUtils;
import com.imizsoft.backendsecurity.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ConfirmationTokenService confirmationTokenService;

    private final PasswordEncoder passwordEncoder;

    private final EmailSender emailSender;

    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, ConfirmationTokenService confirmationTokenService, PasswordEncoder passwordEncoder, EmailSender emailSender, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse authenticateUser(SigninRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
        
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            return new MessageResponse(0, "Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse(0, "Error: Email is already in use!");
        }

        User newUser = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        newUser.setRoles(roles);
        userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                newUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/api/v1/auth/confirm?token="+token;
        emailSender.send(request.getEmail(), buidEmail(request.getUsername(), link));

        return new MessageResponse(1, "We sent an email to confirm your registration");
    }

    @Transactional
    public MessageResponse confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        if(confirmationToken.getConfirmedAt() != null) {
            return new MessageResponse(0, "Email already confirmed!");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiredAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            return new MessageResponse(0, "Token is expired!");
        }
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        userRepository.save(user);
        return new MessageResponse(1, "Registration is confirmed!");
    }

    private String buidEmail(String name, String link){
        String emailBody =
                "<html>\n" +
                        "\t<head>\n" +
                        "\t\t<style type='text/css'>\n" +
                        "\t\t\tbody {margin: 0; padding: 0; min-width: 100%!important;}\n" +
                        "\t\t\thr {border: 1px solid #F9F9F9;}\n" +
                        "\t\t\t.content {width: 100%; max-width: 600px;border-radius:4px; border:1px #dceaf5 solid;}\n" +
                        "\t\t\t.title { color: #fff; font-weight: bold; font-family: Tahoma; font-size:24px; }\n" +
                        "\t\t\t.btn {background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#a395bf), to(#402f61)); font-weight:normal; text-align:center; vertical-align:middle; color:#fff; border-radius:8px; border:1px solid #ddd; padding:10px 70px 10px 70px; text-decoration: none;}\n" +
                        "\t\t\t.btn:hover{text-decoration: underline;}\n" +
                        "\t\t\t.text-muted {color:#fff; margin-left:10px; font-family:sans-serif; font-size:12px; }\n" +
                        "\t\t</style>\n" +
                        "\t</head>\n" +
                        "\t<body>\n" +
                        "\t\t<center>\n" +
                        "\t\t<table class='content' cellpadding='0' cellspacing='0' style='' align='center'>\n" +
                        "\t\t\t<tbody>\n" +
                        "\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t<td style='background-color: #5d6287; padding: 14px 0px 0px 20px;'>\n" +
                        "\t\t\t\t\t\t<h2 class=\"title\">Backend App</h2>\n" +
                        "\t\t\t\t\t</td>\n" +
                        "\t\t\t\t</tr>\t\t\t\t\t\n" +
                        "\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t<td width='100%' style='font-weight: bold; font-family: Tahoma; font-size:24px;' align='center'>\n" +
                        "\t\t\t\t\t\t<br>\n" +
                        "\t\t\t\t\t\t<strong>Confirm your registration</strong>\n" +
                        "\t\t\t\t\t</td>\n" +
                        "\t\t\t\t</tr>\n" +
                        "\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t<td>\n" +
                        "\t\t\t\t\t\t<table cellpadding='0' cellspacing='0' style='line-height:25px;' border='0' align='center'>\n" +
                        "\t\t\t\t\t\t\t<tbody>\n" +
                        "\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t    <td colspan='3' height='30'></td>\n" +
                        "\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t          \t\t\t\t\t<td width='36'></td>\n" +
                        "\t\t          \t\t\t\t\t<td width='454' align='\".$float.\"' style='color:#444444;border-collapse:collapse;font-size:14px;font-family:proxima_nova, Open Sans, Lucida Grande, Segoe UI, Arial, Verdana, Lucida Sans Unicode, Tahoma, Sans Serif; text-align: justify;'>\n" +
                        "\t\t          \t\t\t\t\t\tHi "+name+",\n" +
                        "\t\t          \t\t\t\t\t\t<br><br>Thank you for registring. Please click on the below link to activate your account :\n" +
                        "\t\t          \t\t\t\t\t\t<br><br><center>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<a class='btn' href='"+link+"'>Activate Now</a>\n" +
                        "\t\t\t\t\t\t\t\t\t\t</center>\n" +
                        "\t\t\t\t\t\t\t\t\t\t<br>Link will expire in 15 minutes<br><br>Thanks !\n" +
                        "\t\t          \t\t\t\t\t</td>\n" +
                        "\t\t          \t\t\t\t\t<td width='36'></td>\n" +
                        "\t\t          \t\t\t\t</tr>\n" +
                        "\t\t          \t\t\t\t<tr><td colspan='3' height='36'></td></tr>\n" +
                        "\t\t          \t\t\t</tbody>\n" +
                        "\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t</td>\n" +
                        "\t\t\t\t</tr>\n" +
                        "\t\t\t\t<tr ><td width='100%' height='40px' style='background-color: #b2b5d1;'><p class='text-muted'>&copy; 2021</p></td></tr>\n" +
                        "\t\t\t</tbody>\n" +
                        "\t\t</table></center>\n" +
                        "\t</body>\n" +
                        "</html>";
        return emailBody;
    }

}
