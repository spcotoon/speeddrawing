package com.spcotoon.speeddrawing.member.repository;

import com.spcotoon.speeddrawing.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
