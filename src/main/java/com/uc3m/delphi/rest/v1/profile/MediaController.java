package com.uc3m.delphi.rest.v1.profile;

import com.uc3m.delphi.database.model.Media;
import com.uc3m.delphi.database.repository.MediaRepository;
import com.uc3m.delphi.database.repository.UserRepository;
import com.uc3m.delphi.rest.request.RequestUser;
import com.uc3m.delphi.util.FileUploadUtil;
import com.uc3m.delphi.util.PasswordEncoder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/${v1API}/media")
public class MediaController {
    @Autowired
    private RequestUser requestUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MediaRepository mediaRepository;

    @PostMapping("/upload")
    public Media uploadUserImage(@RequestParam("image") MultipartFile multipartFile) throws IOException {

        // TODO GENERATE STRINGS WITH SERIAL INSTEAD LONG!!
        String uploadDir = "/var/www/delphi/resources";

        Media media = Media.builder()
                .fileName(multipartFile.getOriginalFilename())
                .build();
        Media md = this.mediaRepository.save(media);
        this.fileUploadUtil.saveFile(uploadDir, String.valueOf(media.getId()), multipartFile);
        return md;
    }


    @RequestMapping(value = "/fetch/{id}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public void getMediaImage(HttpServletResponse response, @PathVariable long id) throws IOException {
        System.out.println("ID -> " + id);
        File imgFile = FileUtils.getFile(new File(String.format("%s/%s", "/var/www/delphi/resources", id)));

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(FileUtils.openInputStream(imgFile), response.getOutputStream());
    }


}
