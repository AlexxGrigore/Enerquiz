package server.api;

import commons.Answer;
import commons.IngamePlayer;
import commons.Joker;
import commons.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import server.entities.ServerGame;
import server.services.GameService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    GameService gameService;

    /**
     * Create a new GameController.
     *
     * @param gameService The GameService instance used in this controller
     */
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Get all players in the game the sender is in.
     *
     * @param request The http servlet request with the sender's session information
     * @return 200 OK with a list of players if the sender has a session and is in a game, 400 BAD REQUEST otherwise
     */
    @GetMapping("players")
    public ResponseEntity<List<IngamePlayer>> getPlayers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.badRequest().build();
        }

        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        if (sender == null) {
            return ResponseEntity.badRequest().build();
        }

        ServerGame game = gameService.getGame(sender);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(game.getPlayers());
    }

    /**
     * Remove the intermediary leaderboard from the Ingame scene's mainPane.
     * That mainPane is used to store leaderboards while they are shown.
     *
     * After removing this method calls the method in Server game which is used
     * to continue the game.
     *
     * @param request The http servlet request with the sender's session information
     * @param response The http servlet response with the status code and error message
     */
    @PostMapping("intermediary-leaderboard")
    public void closeIntLeaderboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServerGame game = this.getGame(request, response);
        if (game == null) {
            return;
        }
        game.continueAfterLeaderboard();
    }

    /**
     * Get all questions in the game the sender is in.
     *
     * @param request The http servlet request with the sender's session information
     * @param response The http servlet response with the status code and error message
     * @return 200 OK with a list of questions if the sender has a session and is in a game, 400 BAD REQUEST otherwise
     */
    @GetMapping("questions")
    public List<Question> getQuestion(HttpServletRequest request,
                                                      HttpServletResponse response) throws IOException {
        ServerGame game = this.getGame(request, response);
        if (game == null) {
            return null;
        }

        return game.getQuestions();
    }

    /**
     * Store the sender's answer to the current question. Sending another answer will replace the previous answer.
     *
     * @param request The http servlet request with the sender's session information
     * @param response The http servlet response with the status code and error message
     * @param answer The player's answer
     * @throws IOException
     */
    @PutMapping("answer")
    public void putAnswer(HttpServletRequest request, HttpServletResponse response,
                          @RequestBody Answer answer) throws IOException {

        ServerGame game = this.getGame(request, response);
        if (game == null) {
            return;
        }
        HttpSession session = request.getSession(false);
        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        game.submitAnswer(sender, answer);
    }

    /**
     * Gets game from the session.
     * @param request The http servlet request with the sender's session information.
     * @param response The http servlet response with the status code and error message.
     * @return ServerGame.
     * @throws IOException
     */
    public ServerGame getGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No session");
            return null;
        }

        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        if (sender == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user associated with session");
            return null;
        }

        ServerGame game = gameService.getGame(sender);
        if (game == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not in game");
            return null;
        }

        return game;
    }

    /**
     * Play a joker for the sender.
     *
     * @param request The http servlet request with the sender's session information
     * @param joker The joker to play
     */
    @PostMapping("play-joker/{joker}")
    public void playJoker(HttpServletRequest request, @PathVariable Joker joker) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No session");
        }

        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        if (sender == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user associated with session");
        }

        ServerGame game = gameService.getGame(sender);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not in game");
        }

        if (game.getPlayedJokers(sender).contains(joker)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Joker already played");
        }

        game.playJoker(sender, joker);
    }
}
