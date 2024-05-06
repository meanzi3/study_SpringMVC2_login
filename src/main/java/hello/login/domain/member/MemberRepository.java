package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 동시성 문제가 고려되어 있지 않다. ConcurrentHashMap, AtomicLong 사용을 고려해야 한다.
 */
@Slf4j
@Repository
public class MemberRepository {

  private static Map<Long, Member> store = new HashMap<>();
  private static long sequence = 0L;

  // 저장
  public Member save(Member member){
    member.setId(++sequence);
    log.info("save: member={}", member);
    store.put(member.getId(), member);
    return member;
  }

  // id로 찾기
  public Member findById(Long id){
    return store.get(id);
  }

  // 로그인 id로 찾기
  public Optional<Member> findByLoginId(String loginId){

    return findAll().stream()
            .filter(m -> m.getLoginId().equals(loginId))
            .findFirst();
  }

  // 모든 회원 찾기
  public List<Member> findAll(){
    return new ArrayList<>(store.values());
  }

  // 초기화
  public void clearStore(){
    store.clear();
  }
}
