package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    private final SessionManager sessionManager;

    /*@GetMapping("/")
    public String home() {
        return "home";
    }*/


    /**
     * homeLogin V1
     */
    // @CookieValue 를 사용하면 편리하게 쿠키를 조회할 수 있다.
    // 로그인 하지 않은 사용자도 접근할 수 있기 때문에 required = false 이용
    // @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model){

        // 로그인 쿠키가 없으면 home 으로
        if(memberId == null){
            return "home";
        }

        // 로그인 쿠키가 있어도 회원이 없으면 home 으로
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null){
            return "home";
        }

        // 로그인 쿠키가 있는 사용자는 로그인 사용자 전용 홈 화면인 loginHome 으로 보낸다.
        // 화면에 회원 관련 정보를 출력해야 하므로, 모델에 담아서 member 데이터를 전달한다.
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * homeLogin V2
     */
    // @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model){

        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);
        if(member == null){
            return "home";
        }

        // 로그인 되어 있으면
        model.addAttribute("member", member);
        return "loginHome";
    }

    /**
     * homeLogin V3
     */
    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){
        // 세션이 없으면 home
        HttpSession session = request.getSession(false);
        if(session == null){
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        // 세션에 회원 데이터가 없으면 home
        if(loginMember == null){
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}