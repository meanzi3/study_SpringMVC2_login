package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
  private final LoginService loginService;

  private final SessionManager sessionManager;

  @GetMapping("/login")
  public String loginForm(@ModelAttribute("loginForm") LoginForm form){
    return "login/loginForm";
  }

  /**
   * login V1
   */
  // @PostMapping("/login")
  public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response){
    if(bindingResult.hasErrors()){
      return "login/loginForm";
    }

    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
    log.info("login? {}", loginMember);

    if(loginMember == null){
      bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
      return "login/loginForm";
    }

    // 로그인 성공 처리
    // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료 시 모두 종료)
    Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
    response.addCookie(idCookie);
    // 쿠키를 생성하고 HttpServletResponse 에 담는다. 쿠키 이름은 memberId, 값은 회원의 id를 담아둔다.

    return "redirect:/";
  }

  /**
   * login V2
   */
  // @PostMapping("/login")
  public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response){
    if(bindingResult.hasErrors()){
      return "login/loginForm";
    }

    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
    log.info("login? {}", loginMember);

    if(loginMember == null){
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "login/loginForm";
    }

    // 로그인 성공 처리
    // 세션 관리를 통해 세션을 생성하고, 회원 데이터를 보관한다.
    sessionManager.createSession(loginMember, response);

    return "redirect:/";
  }

  /**
   * login V3
   */
  @PostMapping("/login")
  public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request){
    if(bindingResult.hasErrors()){
      return "login/loginForm";
    }

    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
    log.info("login? {}", loginMember);

    if(loginMember == null){
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "login/loginForm";
    }

    // 로그인 성공 처리

    // 세션이 있으면 있는 세션을 반환하고 없으면 신규 세션을 생성한다.
    HttpSession session = request.getSession();
    // 세션에 로그인 회원 정보를 보관한다.
    session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

    return "redirect:/";
  }

  /**
   * logout v1
   */
  // @PostMapping("/logout")
  public String logout(HttpServletResponse response){
    expireCookie(response, "memberId");
    return "redirect:/";
  }

  /**
   * logout V2
   */
  // @PostMapping("/logout")
  public String logoutV2(HttpServletRequest request){
    sessionManager.expire(request);
    return "redirect:/";
  }

  /**
   * logout V3
   */
  @PostMapping("/logout")
  public String logoutV3(HttpServletRequest request){
    // 세션을 삭제한다.
    // request.getSession(false) : 세션이 있으면 기존 세션을 반환한다.
    // 세션이 없으면 새로운 세션을 생성하지 않는다. null을 반환한다. (create 옵션 : default true)
    HttpSession session = request.getSession(false);
    if(session != null){
      session.invalidate();
    }
    return "redirect:/";
  }

  private void expireCookie(HttpServletResponse response, String cookieName){
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
