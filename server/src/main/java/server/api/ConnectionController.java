package server.api;

import commons.IngamePlayer;
import exceptions.NameException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ConnectionController {
    /**
     * Creates connection for the new user.
     *
     * @param request user request.
     * @return session id.
     * @throws IOException bad request.
     */
    @PostMapping("connect")
    public String createSession(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(true);
        return session.getId();
    }

    /**
     * Request to set users name.
     * @param request user request.
     * @param response response of user request.
     * @param name name to use.
     * @return name if set.
     * @throws NameException bad request.
     */
    @PostMapping("set-name")
    public String setName(HttpServletRequest request, HttpServletResponse response, @RequestParam String name)
            throws NameException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        IngamePlayer user = new IngamePlayer(name);
        session.setAttribute("user", user);


        return name;
    }

    /**
     * Request to change users name.
     * @param request user request.
     * @param response response of user request.
     * @param name name to use.
     * @return name if changed.
     * @throws NameException bad request.
     */
    @PostMapping("change-name")
    public String changeName(HttpServletRequest request, HttpServletResponse response, @RequestParam String name)
            throws NameException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        IngamePlayer sessionUser = (IngamePlayer) session.getAttribute("user");
        sessionUser.setName(name);

        return name;
    }
}
