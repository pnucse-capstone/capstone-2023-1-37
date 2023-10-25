package com.example.p2k.user;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.vm.VmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseUserRepository courseUserRepository;
    private final VmRepository vmRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void save(UserRequest.JoinDTO requestDTO) {
        validateEmailAvailability(requestDTO.getEmailAvailability());

        String enPassword = bCryptPasswordEncoder.encode(requestDTO.getPasswordConf()); // 비밀번호 암호화

        User user = User.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(enPassword)
                .role(requestDTO.getRole())
                .pending(requestDTO.getRole() == Role.ROLE_INSTRUCTOR)
                .build();

        userRepository.save(user);
    }

    public Boolean checkEmail(String email) {
        String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        if(email == null){
            return false;
        }

        Matcher matcher = pattern.matcher(email);
        if(matcher.matches() == false){
            return false;
        }

        return !userRepository.existsByEmail(email);
    }

    public UserResponse.FindByIdDTO findById(Long id){
        User user = getUser(id);
        return new UserResponse.FindByIdDTO(user);
    }

    @Transactional
    public void update(Long userId, UserRequest.UpdateDTO requestDTO){
        User user = getUser(userId);
        user.update(requestDTO.getEmail(), requestDTO.getName());
    }

    @Transactional
    public void resetPassword(UserRequest.ResetDTO requestDTO){
        String email = requestDTO.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );

        validatePasswordMatch(requestDTO.getPassword(), requestDTO.getPasswordConf());

        String enPassword = bCryptPasswordEncoder.encode(requestDTO.getPassword());
        user.updatePassword(enPassword);
    }

    @Transactional
    public void delete(Long id){
        User user = getUser(id);
        courseUserRepository.deleteByUserId(id);
        vmRepository.deleteByUserId(id);
        //TODO: 해당 사용자의 가상 환경 전체 삭제
        userRepository.delete(user);
    }

    private void validatePasswordMatch(String password, String passwordConf) {
        if (!password.equals(passwordConf)) {
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }
    }

    private void validateEmailAvailability(Boolean emailAvailability) {
        if (emailAvailability == null) {
            throw new Exception400("이메일이 확인되지 않았습니다.");
        }

        if (!emailAvailability) {
            throw new Exception400("중복된 이메일입니다.");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
    }
}