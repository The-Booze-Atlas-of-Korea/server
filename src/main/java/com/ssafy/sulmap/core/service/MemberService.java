package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.share.result.Result;

import java.util.Date;

public interface MemberService {
    /// FR1	사용자는 이름, 전화번호, 이메일, 생년월일, 주소, 성별을 입력해 회원가입을 할 수 있어야 한다.
    /// ValidationError 검증되지 않는 파라매터들
    /// ConflictError 중복되는 아이디
    Result registerMember(String id, String password,
                          String name, String phoneNumber, String email, Date birthday, String adress, String sex);
    

}
