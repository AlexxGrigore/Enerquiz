package server.api;

import commons.IngamePlayer;
import commons.MultipleChoiceQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.entities.ServerGame;
import server.services.GameService;
import server.services.GlobalLeaderBoardService;
import server.services.QuestionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueueControllerTest {

    private QueueController controller;
    private HttpServletRequest request;
    private HttpSession session;
    private GameService gameService;
    private QuestionService questionService;
    private GlobalLeaderBoardService globalLeaderBoardService;
    private ServerGame game;

    @BeforeEach
    public void setup() {
        gameService = mock(GameService.class);
        questionService = mock(QuestionService.class);
        globalLeaderBoardService = mock(GlobalLeaderBoardService.class);
        when(questionService.generateRandomQuestions()).thenReturn(
                IntStream.range(0, 20)
                        .mapToObj(i -> new MultipleChoiceQuestion("Question " + i, i % 3))
                        .collect(Collectors.toList()));
        controller = new QueueController(gameService, questionService, globalLeaderBoardService);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        game = mock(ServerGame.class);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    void addPlayer() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        controller.addPlayer(request);
        List<IngamePlayer> pls = controller.getQueue();
        assertTrue(pls.contains(ing));
    }

    @Test
    void leaveQueue() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        controller.addPlayer(request);
        controller.leaveQueue(request);
        List<IngamePlayer> pls = controller.getQueue();
        assertTrue(pls.isEmpty());
    }

    @Test
    void startGame() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        controller.addPlayer(request);
        controller.startGame();
        List<IngamePlayer> pls = controller.getQueue();
        assertTrue(pls.isEmpty());
    }

    @Test
    void startSingle() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        controller.startSingle(request);
        List<IngamePlayer> pls = controller.getQueue();
        assertTrue(pls.isEmpty());
    }

    @Test
    void getStatusTrue() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        when(gameService.getGame(ing)).thenReturn(game);

        ResponseEntity<Boolean> resp = controller.getStatus(request);

        assertEquals(resp, ResponseEntity.ok(true));
    }

    @Test
    void getStatusFalse() {
        IngamePlayer ing = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(ing);
        when(gameService.getGame(ing)).thenReturn(game);
        controller.addPlayer(request);

        ResponseEntity<Boolean> resp = controller.getStatus(request);

        assertEquals(resp, ResponseEntity.ok(false));
    }

}
