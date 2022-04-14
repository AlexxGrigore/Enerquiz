package server.api;

import commons.IngamePlayer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import server.entities.QuizCenter;
import server.entities.ServerGame;
import server.services.GameService;
import server.services.GlobalLeaderBoardService;
import server.services.QuestionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private List<IngamePlayer> queue;
    private QuizCenter center;
    private final transient GameService gameService;
    private final QuestionService questionService;
    private final GlobalLeaderBoardService globalLeaderBoardService;


    /**
     * Constructor.
     * @param gameService service with games.
     * @param questionService service for questions.
     * @param globalLeaderBoardService service for global leaderboard.
     */
    public QueueController(GameService gameService, QuestionService questionService,
                           GlobalLeaderBoardService globalLeaderBoardService) {
        this.queue = new ArrayList<>();
        this.center = new QuizCenter();
        this.gameService = gameService;
        this.questionService = questionService;
        this.globalLeaderBoardService = globalLeaderBoardService;
    }

    /**
     * Adds player to the queue.
     * @param request from the player.
     */
    @PostMapping("add-player")
    public void addPlayer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session does not exist");
        }
        IngamePlayer user = (IngamePlayer) session.getAttribute("user");
        if (this.queue.contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player is already in the queue");
        }
        this.queue.add(user);
    }

    /**
     * Removes player from the queue.
     * @param request from the player.
     */
    @PostMapping("leave-queue")
    public void leaveQueue(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session does not exist");
        }
        IngamePlayer user = (IngamePlayer) session.getAttribute("user");
        if (!this.queue.contains(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player is not in the queue");
        }
        this.queue.remove(user);
    }

    /**
     * Starts a game with the player.
     */
    @PostMapping("start-game")
    public void startGame() {
        ServerGame game = new ServerGame(this.queue, questionService, globalLeaderBoardService, true);
        gameService.addGame(game);
        game.start();
        this.queue = new ArrayList<>();
    }

    /**
     * Starts single player game.
     * @param request request from user.
     */
    @PostMapping("start-single")
    public void startSingle(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session does not exist");
        }

        IngamePlayer user = (IngamePlayer) session.getAttribute("user");
        ServerGame game = new ServerGame(List.of(user), questionService, globalLeaderBoardService, false);
        gameService.addGame(game);
        game.start();
    }

    /**
     * Gets the player queue.
     * @return player queue.
     */
    @GetMapping(path = { "", "/get-queue" })
    public List<IngamePlayer> getQueue() {
        return queue;
    }

    /**
     * Checks if player is in game.
     * @param request player request.
     * @return result.
     */
    @GetMapping(path = { "", "/get-status" })
    public ResponseEntity<Boolean> getStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session does not exist");
        }

        IngamePlayer user = (IngamePlayer) session.getAttribute("user");
        Boolean result = true;
        if (queue.contains(user)) {
            result = false;
        }
        ServerGame sg = this.gameService.getGame(user);
        if (sg == null) {
            result = false;
        }
        return ResponseEntity.ok(result);
    }
}
