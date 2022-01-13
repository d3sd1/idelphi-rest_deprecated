package com.uc3m.delphi.rest.v1.session;

import com.uc3m.delphi.database.dto.ErrorListDto;
import com.uc3m.delphi.database.dto.UserLoginDto;
import com.uc3m.delphi.database.model.PasswordRecover;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.model.UserLogin;
import com.uc3m.delphi.database.repository.PasswordRecoverRepository;
import com.uc3m.delphi.database.repository.UserLoginRepository;
import com.uc3m.delphi.database.repository.UserRepository;
import com.uc3m.delphi.rest.Dto.PasswordRecoverDto;
import com.uc3m.delphi.rest.Dto.PasswordResetDto;
import com.uc3m.delphi.rest.exception.UserException;
import com.uc3m.delphi.util.JwtUtil;
import com.uc3m.delphi.util.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/${v1API}/session")
public class LoginController {

    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;
    private final PasswordRecoverRepository passwordRecoverRepository;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserLoginRepository userLoginRepository,
                           JavaMailSender javaMailSender, PasswordRecoverRepository passwordRecoverRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userLoginRepository = userLoginRepository;
        this.javaMailSender = javaMailSender;
        this.passwordRecoverRepository = passwordRecoverRepository;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public UserLogin doLogin(@RequestBody UserLoginDto userLoginDto) {
        assert userLoginDto != null;
        Optional<User> userOptional = this.userRepository.findByEmailIgnoreCase(userLoginDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorListDto.INVALID_LOGIN.toString());
        }

        User user = userOptional.get();
        if (!this.passwordEncoder.verifyPassword(userLoginDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorListDto.INVALID_LOGIN.toString());
        }
        if (user.isBlocked()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ErrorListDto.USER_BLOCKED.toString());
        }
        this.invalidateUserSessions(user);
        String generatedJwt = this.jwtUtil.generate(user, userLoginDto.isRememberMe());
        UserLogin userLogin = this.userLoginRepository.save(UserLogin.builder()
                .creationDate(LocalDateTime.now())
                .enabled(true)
                .jwt(generatedJwt)
                .user(user)
                .build());
        return userLogin;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/password/recover")
    public void doRecover(@RequestBody PasswordRecoverDto passwordRecoverDto) {
        Optional<User> userOptional = this.userRepository.findByEmailIgnoreCase(passwordRecoverDto.getEmail());
        if (userOptional.isPresent()) {
            long code = 100000 + (new Random()).nextInt(900000);

            this.passwordRecoverRepository.save(PasswordRecover.builder()
                    .expires(LocalDateTime.now().plusMinutes(15))
                    .recoverCode(code)
                    .user(userOptional.get())
                    .build());
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(passwordRecoverDto.getEmail());
            msg.setFrom("no-reply@iventura.net");
            msg.setSubject("Agile Delphi - Recuperación de contraseña");


            msg.setText(String.format("Tu código de recuperación de contraseña es: %s", code));

            javaMailSender.send(msg);
        }

    }


    @RequestMapping(method = RequestMethod.POST, path = "/password/reset")
    public void doReset(@RequestBody PasswordResetDto passwordResetDto) throws UserException {
        Optional<User> userOptional = this.userRepository.findByEmailIgnoreCase(passwordResetDto.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            PasswordRecover passwordRecover = this.passwordRecoverRepository.findByUserAndAndRecoverCode(user, passwordResetDto.getCode()).orElseThrow(() -> new UserException("Invalid recover code."));
            if (passwordRecover.getExpires().isBefore(LocalDateTime.now())) {
                throw new UserException("Recover code is expired");
            }

            user.setNeedsOnboard(true);
            String pass = this.passwordEncoder.generatePlainPassword();
            user.setPassword(this.passwordEncoder.encodePassword(pass));
            this.userRepository.save(user);
            this.passwordRecoverRepository.delete(passwordRecover);

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(passwordResetDto.getEmail());
            msg.setFrom("no-reply@iventura.net");
            msg.setSubject("Agile Delphi - Nueva contraseña");


            msg.setText(String.format("Tu nueva contraseña es: %s", pass));

            javaMailSender.send(msg);
        }

    }

    private void invalidateUserSessions(User user) {
        List<UserLogin> userLogins = this.userLoginRepository.findByUserAndEnabledIsTrue(user);
        for (UserLogin userLogin : userLogins) {
            userLogin.setEnabled(false);
            this.userLoginRepository.save(userLogin);
        }
    }

}
