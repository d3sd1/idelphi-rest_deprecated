package com.uc3m.delphi.rest.v1.process;

import com.uc3m.delphi.database.model.*;
import com.uc3m.delphi.database.repository.*;
import com.uc3m.delphi.rest.Dto.DelphiProcessBasicDataDto;
import com.uc3m.delphi.rest.Dto.QuestionReorderDto;
import com.uc3m.delphi.rest.Dto.RoundBasicDataDto;
import com.uc3m.delphi.rest.exception.DelphiProcessException;
import com.uc3m.delphi.rest.exception.UserException;
import com.uc3m.delphi.rest.request.RequestUser;
import com.uc3m.delphi.rest.service.DelphiProcessService;
import com.uc3m.delphi.util.FileUploadUtil;
import com.uc3m.delphi.util.PasswordEncoder;
import com.uc3m.delphi.ws.model.WsUpdate;
import com.uc3m.delphi.ws.model.WsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/${v1API}/process")
public class ProcessController {

    private final DelphiProcessRepository delphiProcessRepository;
    private final DelphiProcessRoundRepository delphiProcessRoundRepository;
    private final UserRepository userRepository;
    private final DelphiProcessRoundQuestionRepository delphiProcessRoundQuestionRepository;

    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    private RequestUser requestUser;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DelphiProcessRoundQuestionAnswerRepository delphiProcessRoundQuestionAnswerRepository;

    @Autowired
    private DelphiProcessRoundQuestionCategoryRepository delphiProcessRoundQuestionCategoryRepository;

    @Autowired
    private DelphiRespTempRepository delphiRespTempRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private DelphiProcessService delphiProcessService;

    public ProcessController(DelphiProcessRepository delphiProcessRepository, UserRepository userRepository,
                             DelphiProcessRoundRepository delphiProcessRoundRepository,
                             DelphiProcessRoundQuestionRepository delphiProcessRoundQuestionRepository) {
        this.delphiProcessRepository = delphiProcessRepository;
        this.userRepository = userRepository;
        this.delphiProcessRoundRepository = delphiProcessRoundRepository;
        this.delphiProcessRoundQuestionRepository = delphiProcessRoundQuestionRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/list")
    public List<DelphiProcess> getAllProcess() {
        return this.delphiProcessRepository.findAllByCoordinatorsIsContainingOrExpertsIsContaining(requestUser.getUser(), requestUser.getUser());
    }

    /**
     * Search users (as experts) by criteria.
     *
     * @param criteria Search criterial, which may be (most relevant to less):
     *                 - Email
     *                 - Name nor surnames.
     * @return Search criterial results.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/filter")
    public List<User> filterUsers(@RequestParam("criteria") String criteria, @RequestParam("process_id") Long processId) throws DelphiProcessException {
        List<User> users = this.userRepository.findAllByEmailContains(criteria, PageRequest.of(0, 10)).getContent();
        DelphiProcess process = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process not found."));

        users = users.stream().filter(user -> {
            return Stream.concat(process.getCoordinators().stream(), process.getExperts().stream()).noneMatch(pUser -> {
                return pUser.getId() == user.getId();
            });
        }).collect(Collectors.toList());
        System.out.println("users + " + users);
        return users;
    }

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Value("${api.url}")
    private String apiUrl;

    @PostMapping("/photo")
    public Media uploadPhoto(@RequestParam("process_id") Long processId, @RequestParam("image") MultipartFile multipartFile) throws IOException, DelphiProcessException {
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));

        // TODO GENERATE STRINGS WITH SERIAL INSTEAD LONG!!
        String uploadDir = "/var/www/delphi/resources";

        Media media = Media.builder()
                .fileName(multipartFile.getOriginalFilename())
                .build();
        Media md = this.mediaRepository.save(media);
        this.fileUploadUtil.saveFile(uploadDir, String.valueOf(media.getId()), multipartFile);
        delphiProcess.setPictureUrl(this.apiUrl + "/v1/media/fetch/" + media.getId());
        delphiProcess = this.delphiProcessRepository.save(delphiProcess);
        for (User u : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            System.out.println("Notify user: " + u.getEmail());
            template.convertAndSendToUser(String.valueOf(u.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
        return md;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/id/{id}")
    public DelphiProcess filterProcessById(@PathVariable("id") Long id) throws DelphiProcessException {
        return this.delphiProcessRepository.findById(id).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", id)));
    }

    public void sendInvitation(String email, String password) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setFrom("no-reply@iventura.net");
        msg.setSubject("Bienvenido a Agile Delphi");


        msg.setText(String.format("Muchas gracias por su interés en la aplicación. Sus credenciales de acceso son:\nYour email is: %s\nYour password is: %s", email, password));

        javaMailSender.send(msg);
    }

    @Autowired
    private LanguageRepository languageRepository;

    private User inviteUser(String email) throws DelphiProcessException {
        String pass = this.passwordEncoder.generatePlainPassword();
        User user = User.builder()
                .email(email)
                .blocked(false)
                .enabled(true)
                .chatStatus(ChatStatus.OFFLINE)
                .notificationStatus(true)
                .needsOnboard(true)
                .language(this.languageRepository.findById(1L).orElseThrow(() -> new DelphiProcessException("Language with ID 1 not found")))
                .password(this.passwordEncoder.encodePassword(pass)).build();
        User u = this.userRepository.save(user);
        this.sendInvitation(user.getEmail(), pass);
        return u;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/basic")
    public void updateBasicProcess(@RequestParam("process_id") Long processId, @RequestBody DelphiProcessBasicDataDto delphiProcessBasicDataDto) throws DelphiProcessException, UserException {
        DelphiProcess process = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process not found."));

        process.setName(delphiProcessBasicDataDto.getName());
        process.setDescription(delphiProcessBasicDataDto.getDescription());
        process.setObjectives(delphiProcessBasicDataDto.getObjectives());
        process = this.delphiProcessRepository.save(process);
        // Other process users
        for (User user : Stream.concat(process.getExperts().stream(), process.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(process).build());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/current_round/basic")
    public void updateBasicProcess(@RequestParam("process_id") Long processId, @RequestBody RoundBasicDataDto roundBasicDataDto) throws DelphiProcessException, UserException {
        DelphiProcess process = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process not found."));

        if (process.getCurrentRound() == null) {
            process.setCurrentRound(new DelphiProcessRound());
            process.getCurrentRound().setStarted(false);
            process.getCurrentRound().setFinished(false);
            process.getCurrentRound().setOrderPosition(0);
        }
        process.getCurrentRound().setName(roundBasicDataDto.getName());
        process.getCurrentRound().setEndTime(roundBasicDataDto.getEndTime());


        this.delphiProcessRoundRepository.save(process.getCurrentRound());

        // Other process users
        for (User user : Stream.concat(process.getExperts().stream(), process.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(process).build());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rm_user")
    public void rmUserFromProcess(@RequestParam("user_id") Long userId,
                                  @RequestParam("process_id") Long processId) throws DelphiProcessException, UserException {

        User u = this.userRepository.findById(userId).orElseThrow(() -> new UserException("User not found."));

        DelphiProcess process = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process not found."));
        process.setExperts(process.getExperts().stream()
                .filter(x -> x.getId() != u.getId())
                .collect(Collectors.toList()));
        process.setCoordinators(process.getCoordinators().stream()
                .filter(x -> x.getId() != u.getId())
                .collect(Collectors.toList()));
        this.delphiProcessRepository.save(process);

        // Current user (removed from process)

        template.convertAndSendToUser(String.valueOf(u.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                .mode(WsUpdate.REMOVE).data(process).build());
        // Other process users
        for (User user : Stream.concat(process.getExperts().stream(), process.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(process).build());
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/add_user")
    public User addUserToProcess(@RequestParam("email") String email,
                                 @RequestParam("process_id") Long processId,
                                 @RequestParam("type") String type) throws DelphiProcessException, UserException {

        Optional<User> userOptional = this.userRepository.findByEmailIgnoreCase(email);
        User u;
        if (userOptional.isEmpty()) {
            u = this.inviteUser(email);
        } else {
            u = userOptional.get();
        }
        DelphiProcess process = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process not found."));

        if (type.equalsIgnoreCase("expert")) {
            process.getExperts().add(u);
        } else if (type.equalsIgnoreCase("coordinator")) {
            process.getCoordinators().add(u);
        }
        process = this.delphiProcessRepository.save(process);

        for (User user : Stream.concat(process.getExperts().stream(), process.getCoordinators().stream())
                .collect(Collectors.toList())) {
            System.out.println("Notify user: " + user.getEmail());
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(process).build());
        }
        return u;
    }

    /**
     * IN USE! 06062021
     *
     * @param delphiProcess
     * @return
     * @throws DelphiProcessException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "")
    public DelphiProcess saveProcess(@RequestBody DelphiProcess delphiProcess) throws DelphiProcessException {
        delphiProcess.setCoordinators(new ArrayList<User>() {{
            add(requestUser.getUser());
        }});
        delphiProcess.setCreationDate(LocalDateTime.now());
        delphiProcess.setModifiedDate(LocalDateTime.now());
        delphiProcess.setCurrentRound(null);
        DelphiProcess delphiProcessDb = this.delphiProcessRepository.save(delphiProcess);
        for (User user : Stream.concat(delphiProcessDb.getExperts().stream(), delphiProcessDb.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.ADD).data(delphiProcessDb).build());
        }
        return delphiProcessDb;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/round/vote")
    public DelphiProcess voteProcessRound(@RequestParam("process_id") Long processId, @RequestParam("round_id") Long roundId) throws DelphiProcessException {
        //TODO: check permissions for that action on user that is sending the request. permiso experto sobre ronda actual

        //TODO: votar sobre ronda actual.
        //TODO: al votar, sacar experto de expertsRemaining y meterlo a expertsVoted
        //TODO: en caso de que expertsVoted.length == 0, cerrar ronda actual y pasar a la siguiente (todo 2,3 en closeProcessRound)
        return null; //TODO
    }

    private void closeRound(DelphiProcess delphiProcess) {
        delphiProcess.getCurrentRound().setStarted(true);
        delphiProcess.getCurrentRound().setFinished(true);
        if (delphiProcess.getRounds() == null) {
            delphiProcess.setRounds(new ArrayList<>());
        }
        delphiProcess.getCurrentRound().setOrderPosition(delphiProcess.getRounds().size());
        delphiProcess.getRounds().add(delphiProcess.getCurrentRound());
        delphiProcess.setCurrentRound(null);
        delphiProcess = this.delphiProcessRepository.save(delphiProcess);

        // Other process users
        for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/round/close")
    public void closeProcessRound(@RequestParam("process_id") Long processId) throws DelphiProcessException {
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));
        this.closeRound(delphiProcess);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/round/start")
    public void startProcessRound(@RequestParam("process_id") Long processId) throws DelphiProcessException {
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));
        delphiProcess.getCurrentRound().setStarted(true);
        delphiProcess.getCurrentRound().setExpertsVoted(new ArrayList<>());
        delphiProcess.getCurrentRound().setExpertsRemaining(new ArrayList<>(delphiProcess.getExperts()));
        this.delphiProcessRoundRepository.save(delphiProcess.getCurrentRound());
        // Other process users
        for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "/questions/add")
    public void addQuestionToCurrentRound(@RequestParam("process_id") Long processId, @RequestBody DelphiProcessRoundQuestion delphiProcessRoundQuestion) throws DelphiProcessException {
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));

        if (delphiProcess.getCurrentRound() == null) {
            DelphiProcessRound newRound = this.delphiProcessRoundRepository.save(new DelphiProcessRound());
            if (delphiProcess.getRounds() == null) {
                delphiProcess.setRounds(new ArrayList<>());
            }
            newRound.setOrderPosition(delphiProcess.getRounds().size());
            delphiProcess.setCurrentRound(newRound);
            this.delphiProcessRepository.save(delphiProcess);
        }
        if (delphiProcess.getCurrentRound().getQuestions() == null) {
            delphiProcess.getCurrentRound().setQuestions(new HashSet<>());
        }
        delphiProcessRoundQuestion.setOrderPosition(delphiProcess.getCurrentRound().getQuestions().size());
        delphiProcessRoundQuestion.setMaxVal(10);
        delphiProcess.getCurrentRound().getQuestions().add(delphiProcessRoundQuestion);
        this.delphiProcessRoundQuestionRepository.save(delphiProcessRoundQuestion);
        delphiProcess = this.delphiProcessRepository.save(delphiProcess);
        // Other process users
        for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "/end")
    public void endProcess(@RequestParam("process_id") Long processId, @RequestBody DelphiProcess inputDelphiProcess) throws DelphiProcessException {
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));

        delphiProcess.getRounds().forEach((round) -> {
            round.setStarted(true);
            round.setFinished(true);
            this.delphiProcessRoundRepository.save(round);
        });
        delphiProcess.setFinalComment(inputDelphiProcess.getFinalComment());
        delphiProcess.setProcessFinished(true);
        delphiProcess = this.delphiProcessRepository.save(delphiProcess);
// Other process users
        for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/question/update")
    public void updateQuestion(@RequestParam("process_id") Long processId, @RequestBody DelphiProcessRoundQuestion delphiProcessRoundQuestion) throws DelphiProcessException {
        DelphiProcessRoundQuestion delphiProcessRoundQuestionDb = this.delphiProcessRoundQuestionRepository.findById(delphiProcessRoundQuestion.getId()).orElseThrow(() -> new DelphiProcessException("Question not found."));

        delphiProcessRoundQuestionDb.setMaxVal(delphiProcessRoundQuestion.getMaxVal());
        delphiProcessRoundQuestionDb.setName(delphiProcessRoundQuestion.getName());
        delphiProcessRoundQuestionDb.setMinVal(delphiProcessRoundQuestion.getMinVal());
        delphiProcessRoundQuestionDb.setType(delphiProcessRoundQuestion.getType());
        for (DelphiProcessRoundQuestionCategory cat : delphiProcessRoundQuestion.getCategories()) {
            this.delphiProcessRoundQuestionCategoryRepository.save(cat);
        }

        delphiProcessRoundQuestionDb.setMaxSelectable(delphiProcessRoundQuestion.getMaxSelectable());
        delphiProcessRoundQuestionDb.setCategories(delphiProcessRoundQuestion.getCategories());
        delphiProcessRoundQuestionDb = this.delphiProcessRoundQuestionRepository.save(delphiProcessRoundQuestionDb);
        DelphiProcess delphiProcessDb = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process id not found."));
        for (User user : Stream.concat(delphiProcessDb.getExperts().stream(), delphiProcessDb.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcessDb).build());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/question/answers")
    public void endProcess(@RequestParam("process_id") Long processId, @RequestBody DelphiProcessRoundQuestionAnswer[] delphiProcessRoundQuestionAnswers) throws DelphiProcessException {

        DelphiProcess delphiProcessDb = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException("Process id not found."));
        if (delphiProcessDb.getCurrentRound() == null) {
            throw new DelphiProcessException("Process has no active round");
        }
        System.out.println("REMAINING EXPERTS  " + delphiProcessDb.getCurrentRound().getExpertsRemaining());
        if (delphiProcessDb.getCurrentRound().getExpertsRemaining().stream().noneMatch((u) -> u.getId() == this.requestUser.getUser().getId())) {
            throw new DelphiProcessException("User is not allowed to vote");
        }

        List<DelphiProcessRoundQuestionAnswer> delphiProcessRoundQuestionAnswerList = new ArrayList<>();
        for (DelphiProcessRoundQuestionAnswer delphiProcessRoundQuestionAnswer : delphiProcessRoundQuestionAnswers) {
            delphiProcessRoundQuestionAnswer.setAnswerDate(LocalDateTime.now());
            delphiProcessRoundQuestionAnswerList.add(this.delphiProcessRoundQuestionAnswerRepository.save(delphiProcessRoundQuestionAnswer));
        }
        delphiProcessDb.getCurrentRound().getAnswers().addAll(delphiProcessRoundQuestionAnswerList);
        delphiProcessDb.getCurrentRound().getExpertsVoted().add(this.requestUser.getUser());
        delphiProcessDb.getCurrentRound().getExpertsRemaining().removeIf((e) -> e.getId() == this.requestUser.getUser().getId());


        this.delphiProcessRoundRepository.save(delphiProcessDb.getCurrentRound());

        // end round if no more experts remaining
        if (delphiProcessDb.getCurrentRound().getExpertsRemaining().size() == 0) {
            this.closeRound(delphiProcessDb);
        }
        for (User user : Stream.concat(delphiProcessDb.getExperts().stream(), delphiProcessDb.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcessDb).build());
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/current_round/questions/reorder")
    public void endProcess(@RequestParam("process_id") Long processId, @RequestBody QuestionReorderDto questionReorderDto) throws DelphiProcessException {

        System.out.println("aaaaaaaaaaaaaaaa" + questionReorderDto);
        DelphiProcessRoundQuestion from = this.delphiProcessRoundQuestionRepository.findById(questionReorderDto.getFromId()).orElseThrow(() -> new DelphiProcessException("Could not find question on reorder."));
        from.setOrderPosition(questionReorderDto.getToPosition());
        this.delphiProcessRoundQuestionRepository.save(from);

        DelphiProcessRoundQuestion to = this.delphiProcessRoundQuestionRepository.findById(questionReorderDto.getToId()).orElseThrow(() -> new DelphiProcessException("Could not find question on reorder."));
        to.setOrderPosition(questionReorderDto.getFromPosition());
        this.delphiProcessRoundQuestionRepository.save(to);

        // Other process users
        DelphiProcess delphiProcess = this.delphiProcessRepository.findById(processId).orElseThrow(() -> new DelphiProcessException(String.format("Delphi process not found with id %d", processId)));
        for (User user : Stream.concat(delphiProcess.getExperts().stream(), delphiProcess.getCoordinators().stream())
                .collect(Collectors.toList())) {
            template.convertAndSendToUser(String.valueOf(user.getId()), "/ws/subscribe/process/list", WsWrapper.builder()
                    .mode(WsUpdate.MODIFY).data(delphiProcess).build());
        }
    }
}
