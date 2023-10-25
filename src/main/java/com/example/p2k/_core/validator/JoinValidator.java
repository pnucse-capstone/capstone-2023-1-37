package com.example.p2k._core.validator;

import com.example.p2k.user.UserRepository;
import com.example.p2k.user.UserRequest;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Aspect
@Component
public class JoinValidator implements Validator {

    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequest.JoinDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UserRequest.JoinDTO joinDTO = (UserRequest.JoinDTO) obj;
        if(!((UserRequest.JoinDTO) obj).getPassword().equals(((UserRequest.JoinDTO) obj).getPasswordConf())){
            //비밀번호와 비밀번호 확인이 다르다면
            errors.rejectValue("password", "key","비밀번호가 일치하지 않습니다.");
        } else if(userRepository.findByEmail(((UserRequest.JoinDTO) obj).getEmail()) !=null){
            // 이름이 존재하면
            errors.rejectValue("email", "key","이미 사용중인 이메일입니다.");
        }

    }//
}
