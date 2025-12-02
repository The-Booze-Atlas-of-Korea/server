package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.MemberModel;

import java.util.List;

public interface MemberRepository {
    Long insert(MemberModel member);
    Long update(MemberModel member);
    void delete(MemberModel member);
    MemberModel findById(Long id);
    List<MemberModel> findAll();
}
