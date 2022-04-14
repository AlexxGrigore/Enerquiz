package server.api;

import commons.Answer;
import commons.IngamePlayer;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.entities.ServerGame;
import server.services.GameService;
import server.services.GlobalLeaderBoardService;
import server.services.QuestionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class GameControllerTest {

    private GameService service;
    private QuestionService questionService;
    private GlobalLeaderBoardService globalLeaderBoardService;
    private GameController controller;
    private ServerGame serverGame;
    private GameService serviceMock;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setup() {
        questionService = mock(QuestionService.class);
        service = new GameService();
        globalLeaderBoardService = mock(GlobalLeaderBoardService.class);
        serviceMock = mock(GameService.class);
        controller = new GameController(service);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        serverGame = mock(ServerGame.class);


    }

    public void setSession(IngamePlayer player) {
        session.setAttribute("user", player);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(player);

        assertSame(player, session.getAttribute("user"));
    }

    @Test
    void getPlayers() {
        IngamePlayer player = new IngamePlayer("Jeroen");
        setSession(player);

        List<IngamePlayer> players = List.of(
                new IngamePlayer("Player A"),
                new IngamePlayer("Player B"),
                new IngamePlayer("Player C"),
                player
        );

        List<Question> questions = new ArrayList<>();

        service.addGame(new ServerGame(players, questionService, globalLeaderBoardService, true));

        ResponseEntity<List<IngamePlayer>> response = controller.getPlayers(request);
        assertEquals(players, response.getBody());
    }

    @Test
    void getPlayersNoSession() {
        ResponseEntity<List<IngamePlayer>> response = controller.getPlayers(request);

        assertSame(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getPlayersNoGame() {
        IngamePlayer player = new IngamePlayer("Jeroen");
        setSession(player);

        ResponseEntity<List<IngamePlayer>> response = controller.getPlayers(request);

        assertSame(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void sessionNull() throws IOException {
        when(request.getSession(false)).thenReturn(null);

        ServerGame comp = controller.getGame(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "No session");
    }

    @Test
    void senderNull() throws IOException {
        setSession(null);

        ServerGame comp = controller.getGame(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "No user associated with session");
    }

    @Test
    void gameNull() throws IOException {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        setSession(pl1);

        ServerGame comp = controller.getGame(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "User not in game");
    }

    @Test
    void closeIntLeaderboard() throws  IOException {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        setSession(pl1);
        controller = new GameController(serviceMock);
        when(serviceMock.getGame(pl1)).thenReturn(serverGame);

        controller.closeIntLeaderboard(request, response);
        verify(serverGame).continueAfterLeaderboard();
    }

    @Test
    void getQuestion() throws  IOException {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        setSession(pl1);
        controller = new GameController(serviceMock);
        when(serviceMock.getGame(pl1)).thenReturn(serverGame);

        controller.getQuestion(request, response);
        verify(serverGame).getQuestions();
    }

    @Test
    void putAnswer() throws  IOException {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        setSession(pl1);
        controller = new GameController(serviceMock);
        when(serviceMock.getGame(pl1)).thenReturn(serverGame);
        Answer ans = mock(Answer.class);

        controller.putAnswer(request, response, ans);
        verify(serverGame).submitAnswer(pl1, ans);
    }
}
