package com.example.dividends_project.service;

import com.example.dividends_project.exception.impl.AlreadyExistUserException;
import com.example.dividends_project.model.Auth;
import com.example.dividends_project.persist.entity.MemberEntity;
import com.example.dividends_project.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    // password 인코딩
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    // 회원 가입
    public MemberEntity register(Auth.SignUp member) {
        // 중복된 아이디가 있는지 확인
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());
        return result;
    }

    // 로그인 검증
    public MemberEntity authenticate(Auth.SignIn member) {

        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다"));

        // 입력으로 들어온 member 의 비밀번호와 아이디로 찾은 user 의 비밀번호가 일치하는지
        // member 의 비밀번호는 인코딩 되지 않은 비밀번호이고 user 의 비밀번호는 인코딩 된 비밀번호 고로, member 의 비밀번호도 인코딩이 필요
        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
