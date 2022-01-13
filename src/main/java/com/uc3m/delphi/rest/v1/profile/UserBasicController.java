package com.uc3m.delphi.rest.v1.profile;

import com.uc3m.delphi.database.dto.ErrorListDto;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.repository.DelphiProcessRepository;
import com.uc3m.delphi.database.repository.LanguageRepository;
import com.uc3m.delphi.database.repository.UserRepository;
import com.uc3m.delphi.rest.Dto.ResetPass;
import com.uc3m.delphi.rest.exception.LanguageException;
import com.uc3m.delphi.rest.exception.UserException;
import com.uc3m.delphi.rest.request.RequestUser;
import com.uc3m.delphi.util.FileUploadUtil;
import com.uc3m.delphi.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/${v1API}/profile")
public class UserBasicController {
    private final DelphiProcessRepository delphiProcessRepository;
    @Autowired
    private RequestUser requestUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LanguageRepository languageRepository;

    public UserBasicController(DelphiProcessRepository delphiProcessRepository) {
        this.delphiProcessRepository = delphiProcessRepository;
    }

    @PostMapping("/cv")
    public void uploadUserCv(@RequestParam("cv") MultipartFile multipartFile) throws IOException {
        String fileName = String.valueOf(this.requestUser.getUser().getId());
        String uploadDir = "/var/www/delphi/resources/profile_cvs";
        this.fileUploadUtil.deleteFile(uploadDir, fileName);
        this.fileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/onboard")
    public void setOnboardingStatus(@RequestParam boolean status) throws UserException {
        User user = this.userRepository.findById(this.requestUser.getUser().getId())
                .orElseThrow(() -> new UserException(String.format("User not found on database with id %d", this.requestUser.getUser().getId())));
        user.setNeedsOnboard(status);
        this.userRepository.save(user);
    }

    //TODO updating user should bubble to others so it gets update inmediatly
    @RequestMapping(method = RequestMethod.POST, path = "/photo")
    public void setPhoto(@RequestBody User user) {
        this.requestUser.getUser().setPhoto(user.getPhoto());
        this.userRepository.save(this.requestUser.getUser());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/notifications")
    public void setNotificationsStatus(@RequestParam boolean enabled) {
        this.requestUser.getUser().setNotificationStatus(enabled);
        this.userRepository.save(this.requestUser.getUser());
    }


    /**
     * Changes user password.
     * Note that it must be loaded from database since using RequestUser will always use old password to verify.
     * It must load the new password every time.
     *
     * @param resetPass
     */
    @RequestMapping(method = RequestMethod.POST, path = "/change_pass")
    public void setNewPassword(@RequestBody ResetPass resetPass) throws UserException {
        User user = this.userRepository.findById(this.requestUser.getUser().getId())
                .orElseThrow(() -> new UserException(String.format("User not found on database with id %d", this.requestUser.getUser().getId())));
        if (!this.passwordEncoder.verifyPassword(resetPass.getCurrentPass(), user.getPassword())
                && !user.isNeedsOnboard()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, ErrorListDto.INVALID_LOGIN.toString());
        }
        if (!resetPass.getNewPass().equals(resetPass.getNewPassRep())
                && !user.isNeedsOnboard()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, ErrorListDto.INVALID_PASSWORD_REP.toString());
        }

        user.setPassword(this.passwordEncoder.encodePassword(resetPass.getNewPass()));
        this.userRepository.save(user);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/setup")
    public void setupUser(@RequestBody User user) throws UserException {
        User u = this.userRepository.findById(user.getId()).orElseThrow(() -> new UserException("User not found"));
        u.setName(user.getName());
        u.setSurnames(user.getSurnames());
        this.userRepository.save(u);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/lang")
    public void setLanguage(@RequestParam(name = "language_id") Long languageId) throws UserException, LanguageException {
        User user = this.userRepository.findById(this.requestUser.getUser().getId())
                .orElseThrow(() -> new UserException(String.format("User not found on database with id %d", this.requestUser.getUser().getId())));
        user.setLanguage(this.languageRepository.findById(languageId).orElseThrow(() -> new LanguageException(String.format("Language not found on database with id %d", languageId))));
        this.userRepository.save(user);
    }

}
